<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.bank.model.User" %>
<%@ page import="com.bank.model.Account" %>
<%@ page import="com.bank.model.Transaction" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Locale" %>
<%
    User user = (User) session.getAttribute("user");
    Account account = (Account) session.getAttribute("account");
    
    if (user == null || account == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Riwayat Transaksi - Sistem Manajemen Bank</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="dashboard-container">
        <div class="navbar">
            <h2>Riwayat Transaksi</h2>
            <a href="dashboard">Dashboard</a>
        </div>
        
        <div class="card">
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <% if (transactions != null && !transactions.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>Tanggal</th>
                            <th>Jenis</th>
                            <th>Jumlah</th>
                            <th>Rekening Tujuan</th>
                            <th>Keterangan</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Transaction transaction : transactions) { %>
                            <tr>
                                <td><%= dateFormat.format(transaction.getTransactionDate()) %></td>
                                <td>
                                    <span class="transaction-type type-<%= transaction.getTransactionType().toLowerCase().replace("_", "-") %>">
                                        <%= transaction.getTransactionType().replace("_", " ") %>
                                    </span>
                                </td>
                                <td>
                                    <% if (transaction.getTransactionType().equals("DEPOSIT") || transaction.getTransactionType().equals("TRANSFER_IN")) { %>
                                        <span style="color: green;">+ <%= currencyFormat.format(transaction.getAmount()) %></span>
                                    <% } else { %>
                                        <span style="color: red;">- <%= currencyFormat.format(transaction.getAmount()) %></span>
                                    <% } %>
                                </td>
                                <td><%= transaction.getTargetAccount() != null ? transaction.getTargetAccount() : "-" %></td>
                                <td><%= transaction.getDescription() != null ? transaction.getDescription() : "-" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <div class="alert alert-success">
                    Belum ada transaksi.
                </div>
            <% } %>
            
            <a href="dashboard" class="back-link">← Kembali ke Dashboard</a>
        </div>
    </div>
</body>
</html>
