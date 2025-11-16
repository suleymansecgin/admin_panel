# 🔧 Sunucuda index.html Hatası Çözüm Kılavuzu

## ❌ Hata Mesajı
```
GENERAL_EXCEPTION: No static resource /index.html.
```

## 🔍 Sorunun Nedeni

Bu hata, JAR dosyası içinde `index.html` dosyasının bulunmamasından kaynaklanır. Bu genellikle şu durumlarda oluşur:

1. React frontend build edilmemiş
2. Build dosyaları `src/main/resources/static/` klasörüne kopyalanmamış
3. JAR dosyası build edilirken static dosyalar dahil edilmemiş

---

## ✅ Çözüm Adımları

### 1. Sunucuya SSH ile Bağlanın

```bash
ssh kullanici@sunucu_ip
```

### 2. Proje Klasörüne Gidin

```bash
cd /opt/suleymansecgin-proje/admin_panel
# veya
cd /opt/admin-panel/admin_panel
```

### 3. React Frontend'i Build Edin

```bash
# admin_panel-react klasörüne gidin
cd admin_panel-react

# Node modüllerini yükleyin (eğer gerekirse)
npm install

# React uygulamasını build edin
npm run build
```

**✅ Build başarılı kontrolü:**
```bash
ls -la dist/
```

Bu komut `dist` klasörünün içeriğini göstermelidir (`index.html`, `assets/`, vb.).

### 4. Build Dosyalarını Static Klasörüne Kopyalayın

```bash
# Ana proje klasörüne dönün
cd ..

# Static klasörünü oluştur (yoksa)
mkdir -p src/main/resources/static

# Eski dosyaları temizle
rm -rf src/main/resources/static/*

# Yeni build dosyalarını kopyala
cp -r admin_panel-react/dist/* src/main/resources/static/

# Kopyalama başarılı mı kontrol edin
ls -la src/main/resources/static/
```

**✅ Kontrol:** `index.html` dosyası görünmelidir:
```bash
ls -la src/main/resources/static/index.html
```

### 5. Spring Boot JAR Dosyasını Yeniden Oluşturun

```bash
# Maven wrapper'a çalıştırma izni verin
chmod +x mvnw

# JAR dosyasını oluşturun
./mvnw clean package -DskipTests
```

**Not:** Bu işlem 5-10 dakika sürebilir.

**✅ Build başarılı kontrolü:**
```bash
ls -la target/admin_panel-0.0.1-SNAPSHOT.jar
```

### 6. JAR Dosyasını Kopyalayın

```bash
# JAR dosyasını /opt/admin-panel klasörüne kopyalayın
cp target/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/

# Kopyalama başarılı mı kontrol edin
ls -lh /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar
```

### 7. Servisi Yeniden Başlatın

```bash
# Systemd servisini yeniden başlatın
sudo systemctl restart admin-panel

# Servis durumunu kontrol edin
sudo systemctl status admin-panel
```

**✅ Başarılı:** Servis `active (running)` durumunda olmalıdır.

### 8. JAR İçinde index.html'i Kontrol Edin (Opsiyonel)

JAR dosyasının içinde `index.html` dosyasının olduğunu doğrulamak için:

```bash
# JAR dosyasının içeriğini kontrol edin
jar -tf /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar | grep index.html
```

Bu komut `BOOT-INF/classes/static/index.html` gibi bir çıktı göstermelidir.

---

## 🚀 Hızlı Çözüm Script'i

Tüm adımları tek seferde yapmak için:

```bash
#!/bin/bash
cd /opt/suleymansecgin-proje/admin_panel

# React build
cd admin_panel-react
npm install
npm run build

# Static dosyaları kopyala
cd ..
rm -rf src/main/resources/static/*
mkdir -p src/main/resources/static
cp -r admin_panel-react/dist/* src/main/resources/static/

# JAR oluştur
chmod +x mvnw
./mvnw clean package -DskipTests

# JAR'ı kopyala
cp target/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/

# Servisi yeniden başlat
sudo systemctl restart admin-panel
```

---

## 🔄 Otomatik Deployment Script Kullanımı

Projede `deploy.sh` script'i varsa, onu kullanabilirsiniz:

```bash
cd /opt/suleymansecgin-proje/admin_panel
chmod +x deploy.sh
./deploy.sh
```

---

## ⚠️ Önemli Notlar

1. **Node.js Versiyonu:** React build için Node.js 18+ gereklidir
2. **Disk Alanı:** Build işlemi için yeterli disk alanı olduğundan emin olun
3. **İzinler:** `www-data` kullanıcısının JAR dosyasını okuyabilmesi için izinleri kontrol edin
4. **Log Kontrolü:** Hata devam ederse log dosyalarını kontrol edin:
   ```bash
   sudo journalctl -u admin-panel -n 50
   ```

---

## 🐛 Sorun Giderme

### Hata: "npm: command not found"
Node.js kurulu değil. Kurulum için:
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
apt install -y nodejs
```

### Hata: "npm run build" başarısız
React bağımlılıklarını kontrol edin:
```bash
cd admin_panel-react
rm -rf node_modules package-lock.json
npm install
npm run build
```

### Hata: "Permission denied"
İzinleri düzeltin:
```bash
chmod +x mvnw
chmod -R 755 src/main/resources/static
```

### Hata: JAR içinde index.html yok
Static dosyaların kopyalandığından emin olun:
```bash
ls -la src/main/resources/static/index.html
# Dosya görünmelidir
```

---

## ✅ Başarı Kontrolü

1. Tarayıcıda `http://sunucu_ip` adresine gidin
2. Login sayfası görünmelidir
3. Hata mesajı görünmemelidir

Eğer hala hata alıyorsanız, log dosyalarını kontrol edin:
```bash
sudo journalctl -u admin-panel -f
```

