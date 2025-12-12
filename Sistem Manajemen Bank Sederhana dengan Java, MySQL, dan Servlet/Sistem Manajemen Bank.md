# Sistem Manajemen Bank

Sistem manajemen bank sederhana menggunakan Java Servlet, MySQL, dan Tomcat.

## Fitur

1. **Registrasi Pengguna** - Daftar akun baru dengan pembuatan rekening otomatis
2. **Login/Logout** - Autentikasi pengguna dengan session management
3. **Dashboard** - Tampilan informasi rekening dan saldo
4. **Deposit** - Setor uang ke rekening
5. **Tarik Tunai** - Tarik uang dari rekening
6. **Transfer** - Transfer uang ke rekening lain
7. **Riwayat Transaksi** - Lihat histori transaksi lengkap

## Teknologi yang Digunakan

- **Backend**: Java Servlet
- **Database**: MySQL
- **Server**: Apache Tomcat
- **Frontend**: JSP, HTML, CSS
- **JDBC Driver**: MySQL Connector/J 8.0.33

## Struktur Proyek

```
bank-management-system/
├── database.sql                    # Script SQL untuk membuat database
├── bank-system.war                 # File WAR untuk deployment
├── lib/                            # Library dependencies
│   ├── mysql-connector-j-8.0.33.jar
│   └── javax.servlet-api-4.0.1.jar
└── src/main/
    ├── java/com/bank/
    │   ├── model/                  # Model classes
    │   │   ├── User.java
    │   │   ├── Account.java
    │   │   └── Transaction.java
    │   ├── servlet/                # Servlet controllers
    │   │   ├── LoginServlet.java
    │   │   ├── RegisterServlet.java
    │   │   ├── LogoutServlet.java
    │   │   ├── DashboardServlet.java
    │   │   ├── DepositServlet.java
    │   │   ├── WithdrawServlet.java
    │   │   ├── TransferServlet.java
    │   │   └── TransactionServlet.java
    │   └── util/                   # Utility classes
    │       └── DatabaseConnection.java
    └── webapp/
        ├── WEB-INF/
        │   ├── web.xml             # Servlet configuration
        │   ├── classes/            # Compiled Java classes
        │   └── lib/                # Runtime libraries
        ├── css/
        │   └── style.css           # Stylesheet
        ├── login.jsp               # Login page
        ├── register.jsp            # Registration page
        ├── dashboard.jsp           # Dashboard page
        ├── deposit.jsp             # Deposit page
        ├── withdraw.jsp            # Withdraw page
        ├── transfer.jsp            # Transfer page
        └── transactions.jsp        # Transaction history page
```

## Instalasi dan Setup

### 1. Persiapan Database

Pastikan MySQL sudah terinstall dan berjalan. Kemudian jalankan script SQL:

```bash
mysql -u root -p < database.sql
```

Atau login ke MySQL dan jalankan:

```sql
source /path/to/database.sql
```

### 2. Konfigurasi Database Connection

Edit file `src/main/java/com/bank/util/DatabaseConnection.java` sesuai dengan konfigurasi MySQL Anda:

```java
private static final String URL = "jdbc:mysql://localhost:3306/bank_system";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

### 3. Kompilasi Project

```bash
# Buat direktori untuk compiled classes
mkdir -p src/main/webapp/WEB-INF/classes

# Kompilasi semua file Java
javac -cp "lib/*" -d src/main/webapp/WEB-INF/classes \
    src/main/java/com/bank/model/*.java \
    src/main/java/com/bank/util/*.java \
    src/main/java/com/bank/servlet/*.java

# Copy MySQL JDBC driver ke WEB-INF/lib
mkdir -p src/main/webapp/WEB-INF/lib
cp lib/mysql-connector-j-8.0.33.jar src/main/webapp/WEB-INF/lib/
```

### 4. Membuat WAR File

```bash
cd src/main/webapp
jar -cvf ../../../bank-system.war *
cd ../../..
```

### 5. Deploy ke Tomcat

#### Cara 1: Manual Deployment
1. Copy file `bank-system.war` ke direktori `webapps` di Tomcat
2. Restart Tomcat
3. Akses aplikasi di `http://localhost:8080/bank-system/`

#### Cara 2: Tomcat Manager
1. Login ke Tomcat Manager (`http://localhost:8080/manager/html`)
2. Upload file `bank-system.war` melalui interface
3. Deploy aplikasi

## Cara Menggunakan

### 1. Akses Aplikasi

Buka browser dan akses:
```
http://localhost:8080/bank-system/
```

### 2. Login dengan Akun Demo

Sistem sudah menyediakan 2 akun demo:

**Akun 1:**
- Username: `admin`
- Password: `admin123`
- Nomor Rekening: `1001234567`
- Saldo Awal: Rp 5.000.000

**Akun 2:**
- Username: `john_doe`
- Password: `password123`
- Nomor Rekening: `1001234568`
- Saldo Awal: Rp 2.500.000

### 3. Registrasi Akun Baru

1. Klik "Daftar di sini" pada halaman login
2. Isi form registrasi:
   - Username (unik)
   - Password
   - Nama Lengkap
   - Email (unik)
   - Nomor Telepon
3. Sistem akan otomatis membuat nomor rekening
4. Login dengan akun yang baru dibuat

### 4. Fitur-Fitur

#### Deposit
1. Pilih menu "Deposit" dari dashboard
2. Masukkan jumlah uang yang ingin disetor
3. Tambahkan keterangan (opsional)
4. Klik "Deposit"

#### Tarik Tunai
1. Pilih menu "Tarik Tunai" dari dashboard
2. Masukkan jumlah uang yang ingin ditarik
3. Sistem akan validasi saldo mencukupi
4. Tambahkan keterangan (opsional)
5. Klik "Tarik Tunai"

#### Transfer
1. Pilih menu "Transfer" dari dashboard
2. Masukkan nomor rekening tujuan
3. Masukkan jumlah uang yang ingin ditransfer
4. Sistem akan validasi:
   - Nomor rekening tujuan valid
   - Saldo mencukupi
   - Tidak transfer ke rekening sendiri
5. Tambahkan keterangan (opsional)
6. Klik "Transfer"

#### Riwayat Transaksi
1. Pilih menu "Riwayat Transaksi" dari dashboard
2. Lihat semua transaksi yang pernah dilakukan
3. Informasi yang ditampilkan:
   - Tanggal dan waktu transaksi
   - Jenis transaksi
   - Jumlah (+ untuk masuk, - untuk keluar)
   - Rekening tujuan (untuk transfer)
   - Keterangan

## Database Schema

### Tabel: users
| Field | Type | Description |
|-------|------|-------------|
| user_id | INT | Primary key, auto increment |
| username | VARCHAR(50) | Username unik |
| password | VARCHAR(255) | Password (plain text untuk demo) |
| full_name | VARCHAR(100) | Nama lengkap |
| email | VARCHAR(100) | Email unik |
| phone | VARCHAR(20) | Nomor telepon |
| created_at | TIMESTAMP | Waktu pembuatan akun |

### Tabel: accounts
| Field | Type | Description |
|-------|------|-------------|
| account_id | INT | Primary key, auto increment |
| user_id | INT | Foreign key ke users |
| account_number | VARCHAR(20) | Nomor rekening unik |
| account_type | VARCHAR(20) | Jenis rekening (SAVINGS) |
| balance | DECIMAL(15,2) | Saldo rekening |
| status | VARCHAR(20) | Status rekening (ACTIVE) |
| created_at | TIMESTAMP | Waktu pembuatan rekening |

### Tabel: transactions
| Field | Type | Description |
|-------|------|-------------|
| transaction_id | INT | Primary key, auto increment |
| account_id | INT | Foreign key ke accounts |
| transaction_type | VARCHAR(20) | Jenis transaksi |
| amount | DECIMAL(15,2) | Jumlah transaksi |
| target_account | VARCHAR(20) | Rekening tujuan (untuk transfer) |
| description | VARCHAR(255) | Keterangan transaksi |
| transaction_date | TIMESTAMP | Waktu transaksi |

### Jenis Transaksi
- **DEPOSIT**: Setor uang
- **WITHDRAW**: Tarik tunai
- **TRANSFER_OUT**: Transfer keluar
- **TRANSFER_IN**: Transfer masuk

## Keamanan

**CATATAN PENTING**: Sistem ini adalah demo sederhana untuk pembelajaran. Untuk production, implementasikan:

1. **Password Hashing**: Gunakan BCrypt atau algoritma hashing yang aman
2. **SQL Injection Prevention**: Sudah menggunakan PreparedStatement
3. **Session Security**: Tambahkan timeout dan secure flag
4. **Input Validation**: Validasi semua input dari user
5. **HTTPS**: Gunakan HTTPS untuk enkripsi komunikasi
6. **Transaction Isolation**: Gunakan transaction isolation level yang tepat
7. **Logging**: Implementasikan logging untuk audit trail
8. **Error Handling**: Jangan expose error message detail ke user

## Troubleshooting

### Database Connection Error
- Pastikan MySQL berjalan
- Cek username dan password di `DatabaseConnection.java`
- Pastikan database `bank_system` sudah dibuat

### ClassNotFoundException: com.mysql.cj.jdbc.Driver
- Pastikan `mysql-connector-j-8.0.33.jar` ada di `WEB-INF/lib`

### Servlet Not Found
- Pastikan file WAR sudah di-deploy dengan benar
- Cek Tomcat logs di `logs/catalina.out`

### Kompilasi Error
- Pastikan JDK sudah terinstall (minimal Java 8)
- Pastikan classpath sudah benar saat kompilasi

## Requirements

- **Java**: JDK 8 atau lebih tinggi
- **MySQL**: 5.7 atau lebih tinggi
- **Apache Tomcat**: 9.0 atau lebih tinggi
- **Browser**: Chrome, Firefox, Safari, atau Edge (versi terbaru)

## Pengembangan Lebih Lanjut

Beberapa fitur yang bisa ditambahkan:

1. **Admin Panel**: Kelola semua user dan transaksi
2. **Multiple Accounts**: Satu user bisa punya beberapa rekening
3. **Account Types**: Tabungan, giro, deposito
4. **Interest Calculation**: Hitung bunga otomatis
5. **Transaction Limits**: Batasan jumlah transaksi per hari
6. **Email Notifications**: Notifikasi email untuk setiap transaksi
7. **PDF Statement**: Export riwayat transaksi ke PDF
8. **Two-Factor Authentication**: Keamanan tambahan
9. **API REST**: Untuk integrasi dengan aplikasi mobile
10. **Loan Management**: Fitur pinjaman

## Lisensi

Project ini dibuat untuk tujuan pembelajaran.

## Kontak

Jika ada pertanyaan atau masalah, silakan buat issue di repository ini.
