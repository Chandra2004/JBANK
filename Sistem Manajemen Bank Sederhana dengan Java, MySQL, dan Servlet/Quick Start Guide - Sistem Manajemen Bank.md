# Quick Start Guide - Sistem Manajemen Bank

Panduan cepat untuk menjalankan aplikasi dalam 5 menit!

## Prerequisites

✅ Java JDK 8+ terinstall  
✅ MySQL Server terinstall dan berjalan  
✅ Apache Tomcat 9+ terinstall  

## Step 1: Setup Database (2 menit)

```bash
# Login ke MySQL
mysql -u root -p

# Jalankan script database
source /path/to/bank-management-system/database.sql

# Atau langsung dari command line
mysql -u root -p < database.sql
```

**Verifikasi:**
```sql
USE bank_system;
SHOW TABLES;
SELECT * FROM users;
```

Anda akan melihat 3 tabel dan 2 user demo.

## Step 2: Build Application (1 menit)

```bash
cd bank-management-system

# Jalankan build script
./build.sh
```

Output: `bank-system.war` (2.3 MB)

## Step 3: Deploy ke Tomcat (1 menit)

```bash
# Copy WAR file ke Tomcat webapps
cp bank-system.war /path/to/tomcat/webapps/

# Start Tomcat
/path/to/tomcat/bin/startup.sh    # Linux/Mac
# atau
C:\tomcat\bin\startup.bat          # Windows
```

Tomcat akan otomatis extract dan deploy aplikasi.

## Step 4: Access Application (1 menit)

Buka browser dan akses:
```
http://localhost:8080/bank-system/
```

## Login dengan Akun Demo

### Akun 1: Admin
- **Username:** `admin`
- **Password:** `admin123`
- **Saldo:** Rp 5.000.000
- **No. Rekening:** 1001234567

### Akun 2: John Doe
- **Username:** `john_doe`
- **Password:** `password123`
- **Saldo:** Rp 2.500.000
- **No. Rekening:** 1001234568

## Test Fitur

### 1. Login
- Masukkan username dan password
- Klik "Login"

### 2. Lihat Dashboard
- Cek saldo dan informasi rekening
- Lihat menu transaksi

### 3. Deposit
- Klik menu "Deposit"
- Masukkan jumlah: 100000
- Klik "Deposit"
- Cek saldo bertambah

### 4. Transfer
- Klik menu "Transfer"
- Masukkan nomor rekening tujuan: 1001234568 (atau 1001234567)
- Masukkan jumlah: 50000
- Klik "Transfer"
- Cek saldo berkurang

### 5. Lihat Riwayat
- Klik menu "Riwayat Transaksi"
- Lihat semua transaksi yang sudah dilakukan

## Troubleshooting

### ❌ Database connection failed
```bash
# Cek MySQL berjalan
sudo systemctl status mysql    # Linux
net start MySQL                # Windows

# Cek konfigurasi di DatabaseConnection.java
# Default: user=root, password=root
```

### ❌ Port 8080 already in use
```bash
# Cari process yang menggunakan port 8080
lsof -i :8080              # Linux/Mac
netstat -ano | findstr :8080   # Windows

# Kill process atau ubah port Tomcat di conf/server.xml
```

### ❌ 404 Not Found
```bash
# Cek WAR sudah ter-deploy
ls /path/to/tomcat/webapps/bank-system/

# Cek Tomcat logs
tail -f /path/to/tomcat/logs/catalina.out
```

### ❌ ClassNotFoundException
```bash
# Pastikan MySQL JDBC driver ada
ls src/main/webapp/WEB-INF/lib/mysql-connector-j-8.0.33.jar

# Rebuild aplikasi
./build.sh
```

## Konfigurasi Database

Jika menggunakan username/password MySQL yang berbeda, edit file:

**File:** `src/main/java/com/bank/util/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/bank_system";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

Kemudian rebuild:
```bash
./build.sh
```

## Default Paths

### Tomcat
- **Linux:** `/opt/tomcat/` atau `/usr/local/tomcat/`
- **Mac:** `/usr/local/Cellar/tomcat/`
- **Windows:** `C:\tomcat\` atau `C:\Program Files\Apache Software Foundation\Tomcat\`

### MySQL
- **Linux:** `/etc/mysql/`
- **Mac:** `/usr/local/mysql/`
- **Windows:** `C:\Program Files\MySQL\`

## Useful Commands

```bash
# Start Tomcat
/path/to/tomcat/bin/startup.sh

# Stop Tomcat
/path/to/tomcat/bin/shutdown.sh

# View Tomcat logs
tail -f /path/to/tomcat/logs/catalina.out

# Rebuild application
./build.sh

# Check MySQL status
sudo systemctl status mysql

# Access MySQL
mysql -u root -p bank_system
```

## Next Steps

✨ **Berhasil!** Aplikasi sudah berjalan.

Untuk informasi lebih detail:
- **README.md** - Dokumentasi lengkap
- **DEPLOYMENT.md** - Panduan deployment detail
- **database.sql** - Script database

## Support

Jika ada masalah:
1. Cek logs Tomcat di `logs/catalina.out`
2. Cek MySQL error logs
3. Verifikasi semua prerequisites terinstall
4. Pastikan port 8080 dan 3306 tidak digunakan aplikasi lain

---

**Happy Banking! 🏦💰**
