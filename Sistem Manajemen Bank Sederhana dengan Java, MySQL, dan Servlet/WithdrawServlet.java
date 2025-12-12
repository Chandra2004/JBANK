package com.bank.servlet;

import com.bank.model.Account;
import com.bank.util.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WithdrawServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        request.getRequestDispatcher("withdraw.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Account account = (Account) session.getAttribute("account");
        String amountStr = request.getParameter("amount");
        String description = request.getParameter("description");
        
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("error", "Jumlah penarikan harus lebih dari 0");
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
                return;
            }
            
            if (amount.compareTo(account.getBalance()) > 0) {
                request.setAttribute("error", "Saldo tidak mencukupi");
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
                return;
            }
            
            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);
                
                // Update balance
                String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setBigDecimal(1, amount);
                pstmt.setInt(2, account.getAccountId());
                pstmt.executeUpdate();
                pstmt.close();
                
                // Insert transaction
                String transSql = "INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES (?, 'WITHDRAW', ?, ?)";
                pstmt = conn.prepareStatement(transSql);
                pstmt.setInt(1, account.getAccountId());
                pstmt.setBigDecimal(2, amount);
                pstmt.setString(3, description);
                pstmt.executeUpdate();
                pstmt.close();
                
                // Get updated balance
                String selectSql = "SELECT balance FROM accounts WHERE account_id = ?";
                pstmt = conn.prepareStatement(selectSql);
                pstmt.setInt(1, account.getAccountId());
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    account.setBalance(rs.getBigDecimal("balance"));
                    session.setAttribute("account", account);
                }
                rs.close();
                
                conn.commit();
                
                request.setAttribute("success", "Penarikan berhasil! Saldo Anda sekarang: Rp " + account.getBalance());
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
                
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
                request.setAttribute("error", "Terjadi kesalahan sistem: " + e.getMessage());
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Format jumlah tidak valid");
            request.getRequestDispatcher("withdraw.jsp").forward(request, response);
        }
    }
}
