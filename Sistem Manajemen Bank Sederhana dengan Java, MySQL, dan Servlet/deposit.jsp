<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.bank.model.User" %>
<%@ page import="com.bank.model.Account" %>
<%
    User user = (User) session.getAttribute("user");
    Account account = (Account) session.getAttribute("account");
    
    if (user == null || account == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Deposit - Sistem Manajemen Bank</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Deposit</h1>
        <h2>Setor Uang ke Rekening</h2>
        
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
        
        <form action="deposit" method="post">
            <div class="form-group">
                <label for="amount">Jumlah (Rp)</label>
                <input type="number" id="amount" name="amount" min="1" step="0.01" required>
            </div>
            
            <div class="form-group">
                <label for="description">Keterangan</label>
                <input type="text" id="description" name="description" placeholder="Opsional">
            </div>
            
            <button type="submit">Deposit</button>
        </form>
        
        <a href="dashboard" class="back-link">← Kembali ke Dashboard</a>
    </div>
</body>
</html>
