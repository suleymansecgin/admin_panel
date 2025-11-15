# 🚀 Sunucu Güncelleme Kılavuzu

Bu kılavuz, Admin Panel uygulamasını sunucuda güncellemek için adım adım talimatlar içerir.

---

## 📋 Genel Bakış

Sunucu güncellemesi iki ana adımdan oluşur:

1. **Yerel Bilgisayar**: Değişiklikleri Git'e push etme
2. **Sunucu**: Git'ten çekip build ve deploy yapma

---

## 🎯 ADIM 1: Yerel Bilgisayarda Değişiklikleri Git'e Push Etme

### 1.1. Değişiklikleri Kontrol Edin

```bash
git status
```

Bu komut hangi dosyaların değiştiğini gösterir.

### 1.2. Değişiklikleri Stage'e Ekleyin

```bash
git add .
```

Veya belirli dosyaları ekleyin:

```bash
git add admin_panel/admin_panel-react/src/components/Dashboard.jsx
git add admin_panel/deploy.sh
```

### 1.3. Commit Yapın

```bash
git commit -m "Değişiklik açıklaması buraya"
```

**İyi commit mesajı örnekleri:**
- `"Dashboard Bootstrap'e dönüştürüldü"`
- `"Yeni kullanıcı yönetimi özelliği eklendi"`
- `"Bug fix: Login sayfası düzeltildi"`

### 1.4. Git'e Push Edin

```bash
git push
```

**✅ Başarılı push çıktısı:**
```
Enumerating objects: X, done.
Writing objects: 100% (X/X), done.
To https://github.com/suleymansecgin/admin-panel.git
   abc123..def456  main -> main
```

---

## 🎯 ADIM 2: Sunucuda Güncelleme

### 2.1. Sunucuya SSH ile Bağlanın

```bash
ssh root@SUNUCU_IP_ADRESI
```

Veya domain ile:

```bash
ssh root@suleymansecgin.com.tr
```

### 2.2. Proje Dizinine Gidin

```bash
cd /opt/admin-panel/admin_panel
```

**⚠️ ÖNEMLİ:** Script'ler bu dizinden çalışacak şekilde tasarlandı. Mutlaka bu dizinde olmalısınız!

### 2.3. Güncelleme Yöntemleri

İki farklı yöntem var:

---

## 📦 YÖNTEM 1: Otomatik Güncelleme (Önerilen)

Git'ten güncellemeleri çekip otomatik olarak build ve deploy yapar.

### Komut:

```bash
cd /opt/admin-panel/admin_panel
./update.sh
```

### Bu Script Ne Yapar?

1. ✅ Git'ten güncellemeleri çeker (`git pull`)
2. ✅ `deploy.sh` script'ini çalıştırır (aşağıdaki tüm adımlar)

### İlk Kullanımda:

```bash
chmod +x update.sh
```

---

## 📦 YÖNTEM 2: Sadece Build ve Deploy

Eğer Git pull yapmak istemiyorsanız veya zaten manuel olarak pull yaptıysanız:

### Komut:

```bash
cd /opt/admin-panel/admin_panel
./deploy.sh
```

### Bu Script Ne Yapar?

1. ✅ React frontend'i build eder (`npm install` + `npm run build`)
2. ✅ Build çıktısını Spring Boot static klasörüne kopyalar
3. ✅ Spring Boot JAR dosyasını oluşturur (`./mvnw clean package`)
4. ✅ JAR'ı `/opt/admin-panel/` klasörüne kopyalar
5. ✅ Servisi durdurur ve yeniden başlatır (`systemctl restart`)
6. ✅ Servis durumunu kontrol eder

### İlk Kullanımda:

```bash
chmod +x deploy.sh
chmod +x ./mvnw
```

---

## 🔍 Detaylı Adımlar (Manuel Yöntem)

Eğer script'ler çalışmazsa, adım adım manuel yapabilirsiniz:

### 1. Git'ten Güncellemeleri Çekin

```bash
cd /opt/admin-panel/admin_panel

# Yerel değişiklikleri temizle (dikkatli!)
git reset --hard HEAD
git clean -fd

# Git pull yap
git pull
```

### 2. React Frontend'i Build Edin

```bash
cd admin_panel-react

# Paketleri yükle
npm install

# Build yap
npm run build
```

### 3. Build Dosyalarını Kopyalayın

```bash
cd ..

# Static klasörünü oluştur
mkdir -p src/main/resources/static

# Eski dosyaları temizle
rm -rf src/main/resources/static/*

# Yeni build dosyalarını kopyala
cp -r admin_panel-react/dist/* src/main/resources/static/
```

### 4. Spring Boot JAR Oluşturun

```bash
# Maven wrapper'a izin ver
chmod +x ./mvnw

# JAR dosyasını oluştur
./mvnw clean package -DskipTests
```

### 5. JAR'ı Kopyalayın

```bash
# Hedef klasörü oluştur
mkdir -p /opt/admin-panel

# JAR'ı kopyala
cp target/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/
```

### 6. Servisi Yeniden Başlatın

```bash
# Servisi durdur
systemctl stop admin-panel

# 2 saniye bekle
sleep 2

# Servisi başlat
systemctl start admin-panel

# Durumu kontrol et
systemctl status admin-panel
```

---

## 🐛 Sorun Giderme

### ❌ Hata: "Permission denied"

**Çözüm:**
```bash
chmod +x deploy.sh
chmod +x update.sh
chmod +x ./mvnw
```

### ❌ Hata: "update.sh: No such file or directory"

**Çözüm:**
```bash
cd /opt/admin-panel/admin_panel
git pull
chmod +x update.sh
```

### ❌ Hata: "git pull" çakışma hatası veriyor

**Çözüm:**
```bash
# Yerel değişiklikleri at
git reset --hard HEAD
git clean -fd

# Tekrar pull yap
git pull
```

### ❌ Hata: "static/: No such file or directory"

**Çözüm:** Deploy script'i artık otomatik oluşturuyor. Eğer hala sorun varsa:

```bash
mkdir -p src/main/resources/static
```

### ❌ Hata: "./mvnw: Permission denied"

**Çözüm:**
```bash
chmod +x ./mvnw
```

### ❌ Hata: npm install/build hatası

**Çözüm:**
```bash
cd admin_panel-react
rm -rf node_modules package-lock.json
npm install
npm run build
```

### ❌ Servis başlamıyor

**Kontrol Adımları:**

1. **Servis durumunu kontrol edin:**
```bash
systemctl status admin-panel
```

2. **Logları kontrol edin:**
```bash
journalctl -u admin-panel -n 50 --no-pager
```

3. **JAR dosyasının var olduğunu kontrol edin:**
```bash
ls -lh /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar
```

4. **JAR dosyasının tarihini kontrol edin:**
```bash
ls -lh /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar
```
Dosya bugün oluşturulmuş olmalı.

5. **Servisi zorla yeniden başlatın:**
```bash
systemctl stop admin-panel
sleep 3
systemctl start admin-panel
systemctl status admin-panel
```

---

## ✅ Başarı Kontrolü

Güncelleme başarılı olduğunda:

1. ✅ Terminal'de "✅ Deployment tamamlandı!" mesajını görmelisiniz
2. ✅ Servis durumu "active (running)" olmalı
3. ✅ JAR dosyası güncel tarihli olmalı
4. ✅ Web sitesinde değişiklikler görünmeli

### Web Sitesini Kontrol Etme

1. **Tarayıcı cache'ini temizleyin:**
   - `Ctrl + Shift + Delete` → "Cached images and files" → "Clear data"
   - Veya: `Ctrl + F5` (hard refresh)
   - Veya: Gizli modda açın (`Ctrl + Shift + N`)

2. **Web sitesine gidin:**
   - https://suleymansecgin.com.tr
   - Veya domain adresiniz

3. **Değişiklikleri kontrol edin:**
   - Dashboard'un tam ekranı kapladığını kontrol edin
   - Yeni özelliklerin göründüğünü kontrol edin

---

## 📝 Hızlı Referans

### En Sık Kullanılan Komutlar

```bash
# Sunucuya bağlan
ssh root@SUNUCU_IP

# Proje dizinine git
cd /opt/admin-panel/admin_panel

# Otomatik güncelleme (Git pull + deploy)
./update.sh

# Sadece deploy (Git pull yapmadan)
./deploy.sh

# Servis durumunu kontrol et
systemctl status admin-panel

# Servis loglarını görüntüle
journalctl -u admin-panel -n 50 --no-pager

# Servisi yeniden başlat
systemctl restart admin-panel
```

---

## 🎯 Örnek Senaryolar

### Senaryo 1: Dashboard'u Güncelleme

**Yerel bilgisayarda:**
```bash
# Değişiklikleri yap
# ... kod düzenlemeleri ...

# Git'e push et
git add .
git commit -m "Dashboard Bootstrap'e dönüştürüldü"
git push
```

**Sunucuda:**
```bash
cd /opt/admin-panel/admin_panel
./update.sh
```

### Senaryo 2: Sadece Backend Değişikliği

**Yerel bilgisayarda:**
```bash
# Backend kodlarını düzenle
# ... Java dosyalarını düzenle ...

# Git'e push et
git add .
git commit -m "Backend API endpoint'i eklendi"
git push
```

**Sunucuda:**
```bash
cd /opt/admin-panel/admin_panel
./update.sh
```

### Senaryo 3: Acil Düzeltme (Hotfix)

**Sunucuda direkt düzenleme:**
```bash
cd /opt/admin-panel/admin_panel

# Dosyayı düzenle
nano src/main/resources/application.properties

# Sadece deploy yap (Git pull yapmadan)
./deploy.sh
```

---

## ⚠️ Önemli Notlar

1. **Her zaman doğru dizinde olun:**
   - Script'ler `/opt/admin-panel/admin_panel/` dizininden çalışmalı
   - `pwd` komutu ile mevcut dizini kontrol edebilirsiniz

2. **Git pull yapmadan önce:**
   - Yerel değişiklikleriniz varsa, önce commit edin veya stash edin
   - Aksi halde `git pull` çakışma hatası verebilir

3. **Build süresi:**
   - React build: ~5-10 saniye
   - Spring Boot JAR build: ~15-30 saniye
   - Toplam: ~20-40 saniye

4. **Servis yeniden başlatma:**
   - Servis yeniden başlatılırken kısa bir süre (2-3 saniye) erişilemez olabilir
   - Bu normaldir ve endişelenmeyin

5. **Yedekleme:**
   - Önemli değişikliklerden önce JAR dosyasını yedekleyin:
   ```bash
   cp /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar.backup
   ```

---

## 📞 Yardım

Eğer sorun yaşıyorsanız:

1. **Hata mesajını tam olarak okuyun**
2. **Yukarıdaki "Sorun Giderme" bölümüne bakın**
3. **Servis loglarını kontrol edin:**
   ```bash
   journalctl -u admin-panel -n 100 --no-pager
   ```

---

**🎉 Artık sunucu güncellemelerini kolayca yapabilirsiniz!**

