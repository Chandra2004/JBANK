<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.bank.model.User" %>
<%@ page import="com.bank.model.Account" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%
    User user = (User) session.getAttribute("user");
    Account account = (Account) session.getAttribute("account");
    
    if (user == null || account == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Sistem Manajemen Bank</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="dashboard-container">
        <div class="navbar">
            <h2>Sistem Manajemen Bank</h2>
            <a href="logout">Logout</a>
        </div>
        
        <div class="card">
            <h3>Selamat Datang, <%= user.getFullName() %>!</h3>
            
            <div class="info-row">
                <span class="info-label">Nomor Rekening:</span>
                <span class="info-value"><%= account.getAccountNumber() %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Jenis Rekening:</span>
                <span class="info-value"><%= account.getAccountType() %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Status:</span>
                <span class="info-value"><%= account.getStatus() %></span>
            </div>
            
            <div style="text-align: center; margin-top: 30px;">
                <div style="color: #666; font-size: 14px; margin-bottom: 10px;">Saldo Anda</div>
                <div class="balance"><%= currencyFormat.format(account.getBalance()) %></div>
            </div>
        </div>
        
        <div class="card">
            <h3>Menu Transaksi</h3>
            <div class="menu-grid">
                <a href="deposit" class="menu-item">
                    <h3>Deposit</h3>
                    <p>Setor uang ke rekening</p>
                </a>
                
                <a href="withdraw" class="menu-item">
                    <h3>Tarik Tunai</h3>
                    <p>Tarik uang dari rekening</p>
                </a>
                
                <a href="transfer" class="menu-item">
                    <h3>Transfer</h3>
                    <p>Transfer ke rekening lain</p>
                </a>
                
                <a href="transactions" class="menu-item">
                    <h3>Riwayat Transaksi</h3>
                    <p>Lihat histori transaksi</p>
                </a>
            </div>
        </div>
    </div>
</body>
</html>
