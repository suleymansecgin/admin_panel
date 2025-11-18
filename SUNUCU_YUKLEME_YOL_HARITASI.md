# 🚀 Sunucuya Yükleme Yol Haritası

## 📋 Genel Bakış

Bu proje **full-stack** bir uygulamadır:
- **Backend**: Spring Boot (Java 17) - Port 8080
- **Frontend**: React + Vite - Production build
- **Veritabanı**: PostgreSQL
- **Web Sunucusu**: Apache (veya Nginx)
- **Erişim URL'i**: `https://www.suleymansecgin.com.tr/admin_panel`

---

## ✅ ÖN HAZIRLIK KONTROL LİSTESİ

- [ ] DigitalOcean Droplet oluşturuldu (Ubuntu 22.04 LTS önerilir)
- [ ] Droplet IP adresi not edildi
- [ ] SSH anahtarları yapılandırıldı
- [ ] Domain adı hazır (suleymansecgin.com.tr)
- [ ] GitHub repository URL'si hazır

---

## 1️⃣ DOMAIN YÖNLENDİRME (DNS YAPILANDIRMASI)

### 1.1 DigitalOcean'da DNS Kaydı

1. DigitalOcean hesabınıza giriş yapın
2. **Networking** → **Domains** bölümüne gidin
3. **Add Domain** butonuna tıklayın
4. Domain adınızı girin: `suleymansecgin.com.tr`
5. Droplet'inizi seçin (otomatik A kaydı oluşturulur)
6. **www** için de bir A kaydı ekleyin:
   - **Hostname**: `www`
   - **Will direct to**: Droplet'inizi seçin
   - **TTL**: 3600 (varsayılan)

### 1.2 MetuNIC'te Nameserver Yönlendirme

1. MetuNIC hesabınıza giriş yapın
2. Domain yönetim paneline gidin
3. **Nameserver (NS) Kayıtları** bölümünü bulun
4. Nameserver'ları şu şekilde güncelleyin:
   ```
   ns1.digitalocean.com
   ns2.digitalocean.com
   ns3.digitalocean.com
   ```
5. Değişiklikleri kaydedin

**⏱️ Not**: DNS yayılımı 2-48 saat sürebilir. Kontrol için:
```bash
nslookup suleymansecgin.com.tr
```

---

## 2️⃣ SUNUCU HAZIRLIĞI

### 2.1 SSH Bağlantısı

```bash
ssh root@[DROPLET_IP_ADRESI]
# veya
ssh root@suleymansecgin.com.tr
```

### 2.2 Sistem Güncellemesi

```bash
sudo apt update
sudo apt upgrade -y
```

### 2.3 Temel Araçların Kurulumu

```bash
# Git
sudo apt install git -y

# Gerekli kütüphaneler
sudo apt install curl wget unzip -y
```

### 2.4 Java 17 Kurulumu

```bash
# OpenJDK 17 kurulumu
sudo apt install openjdk-17-jdk -y

# Java versiyonunu kontrol et
java -version
# Çıktı: openjdk version "17.0.x"

# JAVA_HOME ayarlama
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$PATH:$JAVA_HOME/bin' >> ~/.bashrc
source ~/.bashrc
```

### 2.5 Maven Kurulumu

```bash
# Maven kurulumu
sudo apt install maven -y

# Maven versiyonunu kontrol et
mvn -version
```

### 2.6 Node.js ve npm Kurulumu (Frontend Build İçin)

```bash
# Node.js 20.x LTS kurulumu
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

# Versiyonları kontrol et
node -v
npm -v
```

### 2.7 PostgreSQL Kurulumu

```bash
# PostgreSQL kurulumu
sudo apt install postgresql postgresql-contrib -y

# PostgreSQL servisini başlat
sudo systemctl start postgresql
sudo systemctl enable postgresql

# PostgreSQL kullanıcısına geçiş ve psql'e bağlan
sudo -u postgres psql

# ⚠️ ÖNEMLİ: Şimdi psql içindesiniz. Prompt şu şekilde görünecek: postgres=#
# Aşağıdaki tüm komutları psql içinde çalıştırın (her satırdan sonra Enter'a basın)

# Veritabanı ve kullanıcı oluştur
CREATE DATABASE admin_panel;
CREATE USER admin_user WITH PASSWORD 'ss207615';
GRANT ALL PRIVILEGES ON DATABASE admin_panel TO admin_user;
ALTER DATABASE admin_panel OWNER TO admin_user;

# admin_panel veritabanına bağlan (psql içinde)
\c admin_panel

# ⚠️ Prompt artık şu şekilde görünecek: admin_panel=#
# Schema oluştur ve yetkilendir
CREATE SCHEMA IF NOT EXISTS admin_panel;
GRANT ALL PRIVILEGES ON SCHEMA admin_panel TO admin_user;
ALTER SCHEMA admin_panel OWNER TO admin_user;

# Tüm tablolar için gelecekteki yetkiler
ALTER DEFAULT PRIVILEGES IN SCHEMA admin_panel GRANT ALL ON TABLES TO admin_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA admin_panel GRANT ALL ON SEQUENCES TO admin_user;

# psql'den çık
\q
```

**Alternatif Yöntem (Tek Komutla):**

Eğer yukarıdaki adımlar karışık geliyorsa, tüm komutları tek seferde çalıştırabilirsiniz:

```bash
sudo -u postgres psql << EOF
CREATE DATABASE admin_panel;
CREATE USER admin_user WITH PASSWORD 'ss207615';
GRANT ALL PRIVILEGES ON DATABASE admin_panel TO admin_user;
ALTER DATABASE admin_panel OWNER TO admin_user;
\c admin_panel
CREATE SCHEMA IF NOT EXISTS admin_panel;
GRANT ALL PRIVILEGES ON SCHEMA admin_panel TO admin_user;
ALTER SCHEMA admin_panel OWNER TO admin_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA admin_panel GRANT ALL ON TABLES TO admin_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA admin_panel GRANT ALL ON SEQUENCES TO admin_user;
\q
EOF
```

**🔐 Güvenlik Notu**: Şifreyi güçlü bir şifre ile değiştirin!

### 2.8 Apache Kurulumu

```bash
# Apache kurulumu
sudo apt install apache2 -y

# Apache modüllerini etkinleştir
sudo a2enmod rewrite
sudo a2enmod proxy
sudo a2enmod proxy_http
sudo a2enmod headers
sudo a2enmod ssl

# Apache'yi başlat
sudo systemctl start apache2
sudo systemctl enable apache2
```

---

## 3️⃣ PROJE DİZİNİ OLUŞTURMA

```bash
# Proje dizini oluştur
sudo mkdir -p /var/www/suleymansecgin.com.tr
sudo chown -R $USER:$USER /var/www/suleymansecgin.com.tr
cd /var/www/suleymansecgin.com.tr
```

---

## 4️⃣ GITHUB PROJESİNİ KLONLAMA

```bash
# Projeyi klonla (GitHub URL'nizi kullanın)
git clone [GITHUB_REPO_URL] .

# Veya eğer private repo ise:
# git clone https://github.com/kullaniciadi/suleymansecgin-proje.git .
```

**Not**: Sonundaki `.` işareti, projenin içeriğini mevcut dizine klonlamak içindir.

---

## 5️⃣ BACKEND KURULUMU VE YAPILANDIRMA

### 5.1 Application Properties Düzenleme

**⚠️ ÖNEMLİ**: Önce projenin klonlandığından emin olun (Bölüm 4).

```bash
# Önce mevcut dizini kontrol edin
pwd
# Çıktı şu şekilde olmalı: /var/www/suleymansecgin.com.tr

# Proje yapısının var olduğunu kontrol edin
ls -la
# admin_panel klasörünü görmelisiniz

# Eğer admin_panel klasörü yoksa, önce projeyi klonlayın (Bölüm 4'e bakın)

# application.properties dosyasının var olduğunu kontrol edin
ls -la admin_panel/src/main/resources/
# application.properties dosyasını görmelisiniz

# Eğer dizin yoksa, oluşturun
mkdir -p admin_panel/src/main/resources

# Şimdi dosyayı düzenleyin
nano admin_panel/src/main/resources/application.properties
```

**Eğer hala "No such file or directory" hatası alıyorsanız:**

```bash
# 1. Tam dizin yolunu kontrol edin
cd /var/www/suleymansecgin.com.tr

# 2. Proje yapısını kontrol edin
ls -la admin_panel/src/main/resources/

# 3. Eğer dosya yoksa, önce projeyi klonlayın
git clone [GITHUB_REPO_URL] .

# 4. Dosyayı oluşturun (eğer yoksa)
touch admin_panel/src/main/resources/application.properties

# 5. Şimdi düzenleyin
nano admin_panel/src/main/resources/application.properties
```

**Düzenlemeniz gereken değerler:**

```properties
# Veritabanı yapılandırması
spring.datasource.url=jdbc:postgresql://localhost:5432/admin_panel
spring.jpa.properties.hibernate.default_schema=admin_panel
spring.datasource.username=admin_user
spring.datasource.password=ss207615

# Server port (8080 kalabilir, Apache reverse proxy kullanacağız)
server.port=8080

# JWT Secret (PRODUCTION İÇİN GÜÇLÜ BİR KEY KULLANIN!)
jwt.secret="7mYu51XtU2UuvUWntKYpYDACIvmHhp5UYpSyRBHxSK8="

# Diğer ayarlar aynı kalabilir
```

**🔐 Güvenlik Uyarısı**: 
- `jwt.secret` için en az 256 bit (32 karakter) güçlü bir key kullanın
- Production ortamında şifreleri environment variables olarak saklayın

### 5.2 Backend Build ve Test

**⚠️ ÖNEMLİ**: Build yapmadan önce proje yapısını kontrol edin.

```bash
# Doğru dizine gidin
cd /var/www/suleymansecgin.com.tr/admin_panel

# pom.xml dosyasının var olduğunu kontrol edin
ls -la pom.xml

# Eğer pom.xml yoksa, proje yapısını kontrol edin
pwd
ls -la

# Eğer admin_panel klasörü boşsa veya yanlış yerdeyseniz:
# 1. Üst dizine çıkın
cd /var/www/suleymansecgin.com.tr

# 2. Proje yapısını kontrol edin
ls -la

# 3. Eğer proje klonlanmamışsa, klonlayın
git clone [GITHUB_REPO_URL] .

# 4. Tekrar admin_panel dizinine gidin
cd admin_panel

# 5. pom.xml'in var olduğunu doğrulayın
ls -la pom.xml
```

**pom.xml dosyası bulunduktan sonra:**

```bash
# Maven ile build
mvn clean package -DskipTests

# Build başarılı olursa, JAR dosyası şurada olacak:
# target/admin_panel-0.0.1-SNAPSHOT.jar
```

**Eğer hala "no POM" hatası alıyorsanız:**

```bash
# 1. Mevcut dizini kontrol edin
pwd
# Çıktı: /var/www/suleymansecgin.com.tr/admin_panel olmalı

# 2. Dosya yapısını kontrol edin
ls -la

# 3. Eğer pom.xml yoksa, projeyi yeniden klonlayın
cd /var/www/suleymansecgin.com.tr
rm -rf admin_panel  # Dikkat: Bu mevcut dosyaları siler!
git clone [GITHUB_REPO_URL] .

# 4. Admin_panel dizinine gidin
cd admin_panel

# 5. pom.xml'i kontrol edin
cat pom.xml | head -20

# 6. Şimdi build yapın
mvn clean package -DskipTests
```

### 5.3 Backend'i Systemd Service Olarak Yapılandırma

```bash
# Systemd service dosyası oluştur
sudo nano /etc/systemd/system/admin-panel.service
```

**Service dosyası içeriği:**

```ini
[Unit]
Description=Admin Panel Spring Boot Application
After=network.target postgresql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/var/www/suleymansecgin.com.tr/admin_panel
ExecStart=/usr/bin/java -jar /var/www/suleymansecgin.com.tr/admin_panel/target/admin_panel-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=admin-panel

[Install]
WantedBy=multi-user.target
```

**Service'i etkinleştir ve başlat:**

```bash
# Systemd'yi yeniden yükle
sudo systemctl daemon-reload

# Service'i etkinleştir
sudo systemctl enable admin-panel

# Service'i başlat
sudo systemctl start admin-panel

# Durumu kontrol et
sudo systemctl status admin-panel

# Logları görüntüle
sudo journalctl -u admin-panel -f
```

---

## 6️⃣ FRONTEND BUILD

### 6.1 Frontend Dependencies Kurulumu

```bash
cd /var/www/suleymansecgin.com.tr/admin_panel/admin_panel-react

# Dependencies kur
npm install
```

### 6.2 Base Path Yapılandırması

Projenin `/admin_panel` altında çalışması için Vite ve React Router yapılandırmasını güncelleyin.

**6.2.1 Vite Config Güncelleme:**

```bash
# Vite config dosyasını düzenle
nano vite.config.js
```

**vite.config.js içeriği:**
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: '/admin_panel/',  // ⚠️ Base path ekleyin
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
```

**6.2.2 React Router Base Path Güncelleme:**

```bash
# App.jsx dosyasını düzenle
nano src/App.jsx
```

**App.jsx içinde Router'a basename ekleyin:**
```javascript
<Router basename="/admin_panel">
  <Routes>
    {/* ... mevcut route'lar ... */}
  </Routes>
</Router>
```

**6.2.3 API Service Yapılandırması:**

```bash
# API service dosyasını kontrol et
nano src/services/api.js
```

**API base URL'i şu şekilde olmalı:**
```javascript
const API_BASE_URL = '/admin_panel/api'; // ⚠️ Base path ile birlikte
```

**6.2.4 Login Redirect Düzeltmesi:**

`src/services/api.js` dosyasındaki login redirect'i de güncelleyin:
```javascript
// Hata durumunda login'e yönlendirme
window.location.href = '/admin_panel/login'  // ⚠️ Base path ile
```

### 6.3 Production Build

```bash
# Production build oluştur
npm run build

# Build çıktısı: admin_panel-react/dist/ klasöründe olacak
```

---

## 7️⃣ APACHE VIRTUAL HOST YAPILANDIRMASI

### 7.1 Virtual Host Dosyası Oluşturma

```bash
# Virtual host dosyası oluştur
sudo nano /etc/apache2/sites-available/suleymansecgin.com.tr.conf
```

**Virtual host yapılandırması:**

```apache
<VirtualHost *:80>
    ServerName suleymansecgin.com.tr
    ServerAlias www.suleymansecgin.com.tr
    ServerAdmin webmaster@suleymansecgin.com.tr

    # Ana DocumentRoot (isteğe bağlı - başka bir site için kullanılabilir)
    DocumentRoot /var/www/suleymansecgin.com.tr

    # /admin_panel için Alias ve Directory yapılandırması
    Alias /admin_panel /var/www/suleymansecgin.com.tr/admin_panel/admin_panel-react/dist

    # Frontend dosyaları için
    <Directory /var/www/suleymansecgin.com.tr/admin_panel/admin_panel-react/dist>
        Options -Indexes +FollowSymLinks
        AllowOverride All
        Require all granted
        
        # React Router için - /admin_panel altında çalışması için
        RewriteEngine On
        RewriteBase /admin_panel/
        RewriteRule ^index\.html$ - [L]
        RewriteCond %{REQUEST_FILENAME} !-f
        RewriteCond %{REQUEST_FILENAME} !-d
        RewriteRule . /admin_panel/index.html [L]
    </Directory>

    # Backend API için reverse proxy - /admin_panel/api altında
    ProxyPreserveHost On
    ProxyPass /admin_panel/api http://localhost:8080/api
    ProxyPassReverse /admin_panel/api http://localhost:8080/api

    # CORS headers (gerekirse)
    Header always set Access-Control-Allow-Origin "*"
    Header always set Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS"
    Header always set Access-Control-Allow-Headers "Authorization, Content-Type"

    # Log dosyaları
    ErrorLog ${APACHE_LOG_DIR}/suleymansecgin-error.log
    CustomLog ${APACHE_LOG_DIR}/suleymansecgin-access.log combined
</VirtualHost>
```

**⚠️ ÖNEMLİ NOTLAR:**
- Proje artık `https://www.suleymansecgin.com.tr/admin_panel` adresinde çalışacak
- Login sayfası: `https://www.suleymansecgin.com.tr/admin_panel/login`
- API endpoint'leri: `https://www.suleymansecgin.com.tr/admin_panel/api/*`

### 7.2 Siteyi Etkinleştirme

```bash
# Varsayılan siteyi devre dışı bırak
sudo a2dissite 000-default.conf

# Yeni siteyi etkinleştir
sudo a2ensite suleymansecgin.com.tr.conf

# Apache yapılandırmasını test et
sudo apache2ctl configtest

# Apache'yi yeniden başlat
sudo systemctl reload apache2
```

### 7.3 İzinleri Ayarlama

```bash
# Apache'nin dosyalara erişebilmesi için
sudo chown -R www-data:www-data /var/www/suleymansecgin.com.tr
sudo chmod -R 755 /var/www/suleymansecgin.com.tr

# Frontend build klasörü için özel izinler
sudo chmod -R 755 /var/www/suleymansecgin.com.tr/admin_panel/admin_panel-react/dist
```

---

## 8️⃣ SSL SERTİFİKASI (HTTPS)

### 8.1 Let's Encrypt Certbot Kurulumu

```bash
# Certbot kurulumu
sudo apt install certbot python3-certbot-apache -y
```

### 8.2 SSL Sertifikası Alma

```bash
# SSL sertifikası al (Apache otomatik yapılandırma)
sudo certbot --apache -d suleymansecgin.com.tr -d www.suleymansecgin.com.tr

# Etkileşimli olarak:
# - Email adresi girin
# - Terms of Service'i kabul edin
# - HTTP'den HTTPS'e yönlendirme seçeneğini seçin (2. seçenek önerilir)
```

### 8.3 Otomatik Yenileme Testi

```bash
# Sertifika yenileme testi
sudo certbot renew --dry-run
```

Certbot otomatik olarak Apache yapılandırmasını güncelleyecek ve HTTPS'i etkinleştirecektir.

---

## 9️⃣ GÜVENLİK YAPILANDIRMASI

### 9.1 Firewall Kurulumu (UFW)

```bash
# UFW kurulumu
sudo apt install ufw -y

# Temel kurallar
sudo ufw default deny incoming
sudo ufw default allow outgoing

# SSH (dikkatli olun, kendinizi kilitlemeyin!)
sudo ufw allow 22/tcp

# HTTP ve HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Firewall'u etkinleştir
sudo ufw enable

# Durumu kontrol et
sudo ufw status
```

### 9.2 Fail2Ban Kurulumu (Brute Force Koruması)

```bash
# Fail2Ban kurulumu
sudo apt install fail2ban -y

# Servisi başlat
sudo systemctl start fail2ban
sudo systemctl enable fail2ban
```

---

## 🔟 GÜNCELLEME VE BAKIM

### 10.1 Güncelleme Script'i Oluşturma

```bash
# Güncelleme script'i oluştur
nano /var/www/suleymansecgin.com.tr/update.sh
```

**Script içeriği:**

```bash
#!/bin/bash

cd /var/www/suleymansecgin.com.tr

# Git'ten son değişiklikleri çek
git pull origin main

# Backend build
cd admin_panel
mvn clean package -DskipTests

# Frontend build
cd ../admin_panel-react
npm install
npm run build

# Service'i yeniden başlat
sudo systemctl restart admin-panel

# Apache'yi yeniden yükle
sudo systemctl reload apache2

echo "Güncelleme tamamlandı!"
```

**Script'i çalıştırılabilir yap:**

```bash
chmod +x /var/www/suleymansecgin.com.tr/update.sh
```

### 10.2 Log Yönetimi

```bash
# Backend logları
sudo journalctl -u admin-panel -f

# Apache logları
sudo tail -f /var/log/apache2/suleymansecgin-error.log
sudo tail -f /var/log/apache2/suleymansecgin-access.log

# PostgreSQL logları
sudo tail -f /var/log/postgresql/postgresql-*.log
```

---

## 1️⃣1️⃣ TEST VE KONTROL

### 11.1 Yerel Test

```bash
# Backend servisinin çalıştığını kontrol et
curl http://localhost:8080/api/health
# veya
sudo systemctl status admin-panel

# Apache'nin çalıştığını kontrol et
sudo systemctl status apache2

# PostgreSQL'in çalıştığını kontrol et
sudo systemctl status postgresql
```

### 11.2 Domain Test

1. Tarayıcıda `https://www.suleymansecgin.com.tr/admin_panel` adresine gidin
2. Frontend'in yüklendiğini kontrol edin
3. Login sayfasını test edin: `https://www.suleymansecgin.com.tr/admin_panel/login`
4. API isteklerinin çalıştığını kontrol edin (Browser DevTools → Network)
   - API istekleri `/admin_panel/api/*` formatında olmalı

### 11.3 SSL Test

```bash
# SSL sertifikasını test et
sudo certbot certificates

# Online SSL test
# https://www.ssllabs.com/ssltest/analyze.html?d=suleymansecgin.com.tr
```

---

## 1️⃣2️⃣ SORUN GİDERME

### Backend Başlamıyor

```bash
# Logları kontrol et
sudo journalctl -u admin-panel -n 50

# Port kullanımını kontrol et
sudo netstat -tulpn | grep 8080

# Java process'lerini kontrol et
ps aux | grep java
```

### Frontend Yüklenmiyor

```bash
# Apache loglarını kontrol et
sudo tail -f /var/log/apache2/suleymansecgin-error.log

# Dosya izinlerini kontrol et
ls -la /var/www/suleymansecgin.com.tr/admin_panel/admin_panel-react/dist

# Apache yapılandırmasını test et
sudo apache2ctl configtest
```

### Veritabanı Bağlantı Hatası

```bash
# PostgreSQL'in çalıştığını kontrol et
sudo systemctl status postgresql

# PostgreSQL'e bağlan
sudo -u postgres psql -d admin_panel

# Kullanıcı yetkilerini kontrol et
\du
```

### API İstekleri Çalışmıyor

```bash
# Reverse proxy yapılandırmasını kontrol et
sudo apache2ctl -S

# Backend'in çalıştığını kontrol et (doğrudan)
curl http://localhost:8080/api/urunler

# Apache üzerinden API'yi test et
curl https://www.suleymansecgin.com.tr/admin_panel/api/urunler

# Apache error loglarını kontrol et
sudo tail -f /var/log/apache2/suleymansecgin-error.log
```

---

## 📝 ÖNEMLİ NOTLAR

1. **Erişim URL'leri**:
   - Ana sayfa: `https://www.suleymansecgin.com.tr/admin_panel`
   - Login sayfası: `https://www.suleymansecgin.com.tr/admin_panel/login`
   - API endpoint'leri: `https://www.suleymansecgin.com.tr/admin_panel/api/*`

2. **Güvenlik**:
   - Production'da güçlü şifreler kullanın
   - JWT secret'ı güvenli tutun
   - Firewall kurallarını düzenli kontrol edin
   - SSL sertifikasını otomatik yenileme yapılandırın

3. **Yedekleme**:
   - Veritabanını düzenli yedekleyin
   - Kod değişikliklerini Git'e commit edin

4. **Performans**:
   - Apache worker sayısını optimize edin
   - PostgreSQL ayarlarını production için optimize edin
   - Frontend build'i production modunda yapın

5. **Monitoring**:
   - Log dosyalarını düzenli kontrol edin
   - Disk kullanımını izleyin
   - CPU ve RAM kullanımını izleyin

---

## ✅ KONTROL LİSTESİ

- [ ] DNS yapılandırması tamamlandı
- [ ] Sunucu hazırlığı yapıldı (Java, Maven, Node.js, PostgreSQL, Apache)
- [ ] Veritabanı oluşturuldu ve yapılandırıldı
- [ ] Proje klonlandı
- [ ] Backend yapılandırıldı ve build edildi
- [ ] Backend systemd service olarak kuruldu
- [ ] Frontend base path yapılandırıldı (/admin_panel)
- [ ] Frontend build edildi
- [ ] Apache virtual host yapılandırıldı (/admin_panel için)
- [ ] SSL sertifikası kuruldu
- [ ] Firewall yapılandırıldı
- [ ] Test edildi ve çalışıyor
- [ ] Güncelleme script'i hazırlandı

---

## 🎯 HIZLI BAŞVURU KOMUTLARI

```bash
# Backend servisini başlat
sudo systemctl start admin-panel

# Backend servisini durdur
sudo systemctl stop admin-panel

# Backend servisini yeniden başlat
sudo systemctl restart admin-panel

# Backend loglarını görüntüle
sudo journalctl -u admin-panel -f

# Apache'yi yeniden başlat
sudo systemctl restart apache2

# Apache yapılandırmasını test et
sudo apache2ctl configtest

# SSL sertifikasını yenile
sudo certbot renew

# Güncelleme script'ini çalıştır
/var/www/suleymansecgin.com.tr/update.sh
```

---

**🎉 Başarılar! Projeniz artık canlıda!**

Sorularınız veya sorunlarınız için log dosyalarını kontrol etmeyi unutmayın.

