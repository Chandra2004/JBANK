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
    <title>Tarik Tunai - Sistem Manajemen Bank</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Tarik Tunai</h1>
        <h2>Tarik Uang dari Rekening</h2>
        
        <div class="alert alert-success">
            Saldo Anda saat ini: <strong><%= currencyFormat.format(account.getBalance()) %></strong>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <form action="withdraw" method="post">
            <div class="form-group">
                <label for="amount">Jumlah (Rp)</label>
                <input type="number" id="amount" name="amount" min="1" step="0.01" required>
            </div>
            
            <div class="form-group">
                <label for="description">Keterangan</label>
                <input type="text" id="description" name="description" placeholder="Opsional">
            </div>
            
            <button type="submit">Tarik Tunai</button>
        </form>
        
        <a href="dashboard" class="back-link">← Kembali ke Dashboard</a>
    </div>
</body>
</html>
