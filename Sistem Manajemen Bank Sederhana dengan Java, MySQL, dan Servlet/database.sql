-- Database untuk Sistem Manajemen Bank
CREATE DATABASE IF NOT EXISTS bank_system;
USE bank_system;

-- Tabel untuk menyimpan data pengguna/nasabah
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel untuk menyimpan data rekening
CREATE TABLE IF NOT EXISTS accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(20) DEFAULT 'SAVINGS',
    balance DECIMAL(15, 2) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Tabel untuk menyimpan riwayat transaksi
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    target_account VARCHAR(20),
    description VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Insert data sample untuk testing
INSERT INTO users (username, password, full_name, email, phone) VALUES
('admin', 'admin123', 'Administrator', 'admin@bank.com', '081234567890'),
('john_doe', 'password123', 'John Doe', 'john@email.com', '081234567891');

INSERT INTO accounts (user_id, account_number, account_type, balance) VALUES
(1, '1001234567', 'SAVINGS', 5000000.00),
(2, '1001234568', 'SAVINGS', 2500000.00);

INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES
(1, 'DEPOSIT', 5000000.00, 'Initial deposit'),
(2, 'DEPOSIT', 2500000.00, 'Initial deposit');
