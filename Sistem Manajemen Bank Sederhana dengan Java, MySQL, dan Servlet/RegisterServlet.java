package com.bank.servlet;

import com.bank.util.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert user
            String userSql = "INSERT INTO users (username, password, full_name, email, phone) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Ambil user_id yang baru dibuat
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    
                    // Generate nomor rekening
                    String accountNumber = generateAccountNumber();
                    
                    // Insert account
                    String accountSql = "INSERT INTO accounts (user_id, account_number, account_type, balance) VALUES (?, ?, 'SAVINGS', 0.00)";
                    PreparedStatement accountStmt = conn.prepareStatement(accountSql);
                    accountStmt.setInt(1, userId);
                    accountStmt.setString(2, accountNumber);
                    accountStmt.executeUpdate();
                    accountStmt.close();
                    
                    conn.commit();
                    
                    request.setAttribute("success", "Registrasi berhasil! Silakan login.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                } else {
                    conn.rollback();
                    request.setAttribute("error", "Gagal membuat akun");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                }
                generatedKeys.close();
            } else {
                conn.rollback();
                request.setAttribute("error", "Gagal registrasi");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            
            String errorMsg = "Terjadi kesalahan sistem";
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("username")) {
                    errorMsg = "Username sudah digunakan";
                } else if (e.getMessage().contains("email")) {
                    errorMsg = "Email sudah terdaftar";
                }
            }
            
            request.setAttribute("error", errorMsg);
            request.getRequestDispatcher("register.jsp").forward(request, response);
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
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("register.jsp");
    }
    
    private String generateAccountNumber() {
        Random random = new Random();
        long number = 1000000000L + (long)(random.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }
}
