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

public class TransferServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        request.getRequestDispatcher("transfer.jsp").forward(request, response);
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
        String targetAccountNumber = request.getParameter("targetAccount");
        String amountStr = request.getParameter("amount");
        String description = request.getParameter("description");
        
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("error", "Jumlah transfer harus lebih dari 0");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                return;
            }
            
            if (amount.compareTo(account.getBalance()) > 0) {
                request.setAttribute("error", "Saldo tidak mencukupi");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                return;
            }
            
            if (targetAccountNumber.equals(account.getAccountNumber())) {
                request.setAttribute("error", "Tidak dapat transfer ke rekening sendiri");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                return;
            }
            
            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);
                
                // Cek apakah target account ada
                String checkSql = "SELECT account_id, balance FROM accounts WHERE account_number = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setString(1, targetAccountNumber);
                ResultSet rs = pstmt.executeQuery();
                
                if (!rs.next()) {
                    rs.close();
                    pstmt.close();
                    conn.rollback();
                    request.setAttribute("error", "Nomor rekening tujuan tidak ditemukan");
                    request.getRequestDispatcher("transfer.jsp").forward(request, response);
                    return;
                }
                
                int targetAccountId = rs.getInt("account_id");
                rs.close();
                pstmt.close();
                
                // Kurangi saldo pengirim
                String deductSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                pstmt = conn.prepareStatement(deductSql);
                pstmt.setBigDecimal(1, amount);
                pstmt.setInt(2, account.getAccountId());
                pstmt.executeUpdate();
                pstmt.close();
                
                // Tambah saldo penerima
                String addSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
                pstmt = conn.prepareStatement(addSql);
                pstmt.setBigDecimal(1, amount);
                pstmt.setInt(2, targetAccountId);
                pstmt.executeUpdate();
                pstmt.close();
                
                // Insert transaction untuk pengirim
                String transSql = "INSERT INTO transactions (account_id, transaction_type, amount, target_account, description) VALUES (?, 'TRANSFER_OUT', ?, ?, ?)";
                pstmt = conn.prepareStatement(transSql);
                pstmt.setInt(1, account.getAccountId());
                pstmt.setBigDecimal(2, amount);
                pstmt.setString(3, targetAccountNumber);
                pstmt.setString(4, description);
                pstmt.executeUpdate();
                pstmt.close();
                
                // Insert transaction untuk penerima
                transSql = "INSERT INTO transactions (account_id, transaction_type, amount, target_account, description) VALUES (?, 'TRANSFER_IN', ?, ?, ?)";
                pstmt = conn.prepareStatement(transSql);
                pstmt.setInt(1, targetAccountId);
                pstmt.setBigDecimal(2, amount);
                pstmt.setString(3, account.getAccountNumber());
                pstmt.setString(4, description);
                pstmt.executeUpdate();
                pstmt.close();
                
                // Get updated balance
                String selectSql = "SELECT balance FROM accounts WHERE account_id = ?";
                pstmt = conn.prepareStatement(selectSql);
                pstmt.setInt(1, account.getAccountId());
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    account.setBalance(rs.getBigDecimal("balance"));
                    session.setAttribute("account", account);
                }
                rs.close();
                
                conn.commit();
                
                request.setAttribute("success", "Transfer berhasil! Saldo Anda sekarang: Rp " + account.getBalance());
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                
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
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
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
            request.getRequestDispatcher("transfer.jsp").forward(request, response);
        }
    }
}
