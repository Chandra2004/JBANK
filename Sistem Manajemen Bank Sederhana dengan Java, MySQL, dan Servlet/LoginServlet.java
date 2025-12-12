package com.bank.servlet;

import com.bank.model.User;
import com.bank.model.Account;
import com.bank.util.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Validasi user
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Login berhasil
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                
                // Ambil data akun
                String accountSql = "SELECT * FROM accounts WHERE user_id = ?";
                PreparedStatement accountStmt = conn.prepareStatement(accountSql);
                accountStmt.setInt(1, user.getUserId());
                ResultSet accountRs = accountStmt.executeQuery();
                
                if (accountRs.next()) {
                    Account account = new Account();
                    account.setAccountId(accountRs.getInt("account_id"));
                    account.setAccountNumber(accountRs.getString("account_number"));
                    account.setAccountType(accountRs.getString("account_type"));
                    account.setBalance(accountRs.getBigDecimal("balance"));
                    account.setStatus(accountRs.getString("status"));
                    
                    // Simpan ke session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    session.setAttribute("account", account);
                    
                    accountRs.close();
                    accountStmt.close();
                    
                    response.sendRedirect("dashboard");
                } else {
                    request.setAttribute("error", "Akun tidak ditemukan");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
            } else {
                // Login gagal
                request.setAttribute("error", "Username atau password salah");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Terjadi kesalahan sistem: " + e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}
