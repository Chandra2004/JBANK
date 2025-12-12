# Panduan Deployment ke Apache Tomcat

## Persiapan

### 1. Install Apache Tomcat

#### Windows:
1. Download Tomcat dari https://tomcat.apache.org/download-90.cgi
2. Extract ke direktori, misalnya `C:\tomcat`
3. Set environment variable `CATALINA_HOME` ke direktori Tomcat

#### Linux/Mac:
```bash
# Download Tomcat
wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.82/bin/apache-tomcat-9.0.82.tar.gz

# Extract
tar -xzf apache-tomcat-9.0.82.tar.gz

# Pindahkan ke /opt
sudo mv apache-tomcat-9.0.82 /opt/tomcat

# Set permissions
sudo chmod +x /opt/tomcat/bin/*.sh
```

### 2. Install MySQL

#### Windows:
1. Download MySQL Installer dari https://dev.mysql.com/downloads/installer/
2. Install MySQL Server
3. Catat username dan password root

#### Linux:
```bash
sudo apt-get update
sudo apt-get install mysql-server
sudo mysql_secure_installation
```

#### Mac:
```bash
brew install mysql
brew services start mysql
```

### 3. Setup Database

```bash
# Login ke MySQL
mysql -u root -p

# Atau jalankan script langsung
mysql -u root -p < database.sql
```

## Deployment Steps

### Metode 1: Deploy WAR File (Recommended)

1. **Copy WAR file ke Tomcat webapps**
   ```bash
   # Linux/Mac
   cp bank-system.war /opt/tomcat/webapps/
   
   # Windows
   copy bank-system.war C:\tomcat\webapps\
   ```

2. **Start Tomcat**
   ```bash
   # Linux/Mac
   /opt/tomcat/bin/startup.sh
   
   # Windows
   C:\tomcat\bin\startup.bat
   ```

3. **Verifikasi Deployment**
   - Tomcat akan otomatis extract WAR file
   - Cek di `webapps/bank-system/` apakah sudah ter-extract
   - Akses aplikasi di `http://localhost:8080/bank-system/`

### Metode 2: Deploy via Tomcat Manager

1. **Enable Tomcat Manager**
   
   Edit file `conf/tomcat-users.xml`:
   ```xml
   <tomcat-users>
     <role rolename="manager-gui"/>
     <role rolename="manager-script"/>
     <user username="admin" password="admin" roles="manager-gui,manager-script"/>
   </tomcat-users>
   ```

2. **Restart Tomcat**

3. **Access Tomcat Manager**
   - Buka `http://localhost:8080/manager/html`
   - Login dengan username: admin, password: admin

4. **Deploy WAR**
   - Scroll ke section "WAR file to deploy"
   - Choose file: pilih `bank-system.war`
   - Click "Deploy"

### Metode 3: Manual Deployment

1. **Extract WAR file**
   ```bash
   mkdir bank-system
   cd bank-system
   jar -xvf ../bank-system.war
   ```

2. **Copy ke webapps**
   ```bash
   cp -r bank-system /opt/tomcat/webapps/
   ```

3. **Restart Tomcat**

## Konfigurasi

### 1. Database Connection

Edit `WEB-INF/classes/com/bank/util/DatabaseConnection.class` atau recompile dengan konfigurasi yang sesuai:

```java
private static final String URL = "jdbc:mysql://localhost:3306/bank_system";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

### 2. Port Configuration

Jika port 8080 sudah digunakan, edit `conf/server.xml`:

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

Ubah `port="8080"` ke port yang diinginkan, misalnya `port="9090"`.

### 3. Memory Configuration

Untuk aplikasi yang lebih besar, edit `bin/catalina.sh` (Linux/Mac) atau `bin/catalina.bat` (Windows):

```bash
# Linux/Mac
export CATALINA_OPTS="$CATALINA_OPTS -Xms512m -Xmx1024m"

# Windows
set CATALINA_OPTS=%CATALINA_OPTS% -Xms512m -Xmx1024m
```

## Testing

### 1. Cek Tomcat Status

```bash
# Linux/Mac
ps aux | grep tomcat

# Windows
tasklist | findstr java
```

### 2. Cek Logs

```bash
# Linux/Mac
tail -f /opt/tomcat/logs/catalina.out

# Windows
type C:\tomcat\logs\catalina.out
```

### 3. Test Application

1. Buka browser
2. Akses `http://localhost:8080/bank-system/`
3. Login dengan akun demo:
   - Username: `admin`
   - Password: `admin123`

## Troubleshooting

### Problem: Port already in use

**Solution:**
```bash
# Linux/Mac - Cari process yang menggunakan port 8080
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Problem: Database connection failed

**Solution:**
1. Cek MySQL berjalan:
   ```bash
   # Linux/Mac
   sudo systemctl status mysql
   
   # Windows
   net start MySQL
   ```

2. Cek username dan password di `DatabaseConnection.java`

3. Cek database sudah dibuat:
   ```sql
   SHOW DATABASES;
   USE bank_system;
   SHOW TABLES;
   ```

### Problem: ClassNotFoundException

**Solution:**
- Pastikan `mysql-connector-j-8.0.33.jar` ada di `WEB-INF/lib/`
- Restart Tomcat

### Problem: 404 Not Found

**Solution:**
1. Cek WAR file sudah ter-deploy:
   ```bash
   ls /opt/tomcat/webapps/
   ```

2. Cek logs untuk error:
   ```bash
   tail -f /opt/tomcat/logs/catalina.out
   ```

3. Pastikan context path benar: `/bank-system/`

### Problem: Session timeout too short

**Solution:**
Edit `WEB-INF/web.xml`:
```xml
<session-config>
    <session-timeout>60</session-timeout> <!-- 60 minutes -->
</session-config>
```

## Production Deployment

### 1. Security Hardening

1. **Change default passwords**
2. **Enable HTTPS**:
   ```xml
   <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
              maxThreads="150" SSLEnabled="true">
       <SSLHostConfig>
           <Certificate certificateKeystoreFile="conf/localhost-rsa.jks"
                       type="RSA" />
       </SSLHostConfig>
   </Connector>
   ```

3. **Remove default webapps**:
   ```bash
   rm -rf /opt/tomcat/webapps/ROOT
   rm -rf /opt/tomcat/webapps/docs
   rm -rf /opt/tomcat/webapps/examples
   ```

4. **Disable directory listing**:
   Edit `conf/web.xml`, set `listings` to `false`

### 2. Performance Tuning

1. **Connection Pooling**:
   Gunakan JNDI DataSource di `context.xml`:
   ```xml
   <Resource name="jdbc/BankDB" 
             auth="Container"
             type="javax.sql.DataSource"
             maxTotal="100" 
             maxIdle="30"
             maxWaitMillis="10000"
             username="root" 
             password="password"
             driverClassName="com.mysql.cj.jdbc.Driver"
             url="jdbc:mysql://localhost:3306/bank_system"/>
   ```

2. **Enable Compression**:
   Edit `conf/server.xml`:
   ```xml
   <Connector port="8080" protocol="HTTP/1.1"
              compression="on"
              compressionMinSize="2048"
              noCompressionUserAgents="gozilla, traviata"
              compressableMimeType="text/html,text/xml,text/plain,text/css,text/javascript,application/javascript"/>
   ```

### 3. Monitoring

1. **Enable JMX**:
   ```bash
   export CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote"
   ```

2. **Setup logging**:
   Edit `conf/logging.properties`

### 4. Backup Strategy

```bash
# Backup database
mysqldump -u root -p bank_system > bank_system_backup.sql

# Backup application
tar -czf bank-system-backup.tar.gz /opt/tomcat/webapps/bank-system/
```

## Undeploy

### Via Tomcat Manager:
1. Access manager: `http://localhost:8080/manager/html`
2. Find "bank-system" in applications list
3. Click "Undeploy"

### Manual:
```bash
# Stop Tomcat
/opt/tomcat/bin/shutdown.sh

# Remove application
rm -rf /opt/tomcat/webapps/bank-system
rm /opt/tomcat/webapps/bank-system.war

# Start Tomcat
/opt/tomcat/bin/startup.sh
```

## Additional Resources

- Apache Tomcat Documentation: https://tomcat.apache.org/tomcat-9.0-doc/
- MySQL Documentation: https://dev.mysql.com/doc/
- Java Servlet Specification: https://javaee.github.io/servlet-spec/

## Support

Jika mengalami masalah saat deployment, cek:
1. Tomcat logs di `logs/catalina.out`
2. Application logs
3. MySQL error logs
