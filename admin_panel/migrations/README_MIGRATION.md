# Veritabanı Migration Kılavuzu

## 🔧 products Tablosuna user_id Kolonu Ekleme

Bu migration script'i, `products` tablosuna `user_id` kolonunu ekler ve `users` tablosu ile foreign key ilişkisi kurar.

---

## 📋 Adım Adım Uygulama

### 1. PostgreSQL'e Bağlanın

**Development (Yerel):**
```bash
sudo -u postgres psql -d postgres
```

**Production (Sunucu):**
```bash
sudo -u postgres psql -d admin_panel_db
```

### 2. Schema'ya Geçin

```sql
SET search_path TO admin_panel;
```

veya

```sql
\c postgres  -- Development için
-- veya
\c admin_panel_db  -- Production için
```

### 3. Mevcut Tablo Yapısını Kontrol Edin

```sql
\d products
```

Bu komut `products` tablosunun mevcut yapısını gösterir.

### 4. Migration Script'ini Çalıştırın

**Yöntem 1: SQL dosyasını doğrudan çalıştırma**

Terminal'den (PostgreSQL'e bağlı değilken):
```bash
sudo -u postgres psql -d postgres -f migrations/add_user_id_to_products.sql
```

**Yöntem 2: SQL komutlarını manuel çalıştırma**

PostgreSQL konsolunda (`psql` içindeyken):
```sql
-- Kolonu ekle
ALTER TABLE admin_panel.products 
ADD COLUMN user_id BIGINT;

-- Foreign key constraint ekle
ALTER TABLE admin_panel.products 
ADD CONSTRAINT fk_product_user 
FOREIGN KEY (user_id) 
REFERENCES admin_panel.users(id) 
ON DELETE CASCADE;

-- Index ekle (performans için)
CREATE INDEX idx_products_user_id ON admin_panel.products(user_id);
```

### 5. Mevcut Ürünlere Kullanıcı Ataması (Opsiyonel)

Eğer veritabanında zaten ürünler varsa, bunlara bir kullanıcı atamanız gerekebilir:

```sql
-- Önce kullanıcıları kontrol edin
SELECT id, username, role FROM admin_panel.users;

-- Mevcut ürünlere ilk kullanıcıyı atayın (veya admin kullanıcısını)
UPDATE admin_panel.products 
SET user_id = (SELECT id FROM admin_panel.users ORDER BY id LIMIT 1)
WHERE user_id IS NULL;
```

### 6. Kolonu NOT NULL Yapma (Opsiyonel)

Eğer tüm ürünlere kullanıcı atadıysanız, kolonu zorunlu yapabilirsiniz:

```sql
ALTER TABLE admin_panel.products 
ALTER COLUMN user_id SET NOT NULL;
```

**⚠️ DİKKAT:** Bu komutu çalıştırmadan önce tüm ürünlerin bir `user_id` değerine sahip olduğundan emin olun!

### 7. Değişiklikleri Kontrol Edin

```sql
-- Tablo yapısını kontrol edin
\d products

-- Ürünleri ve kullanıcılarını görüntüleyin
SELECT p.id, p.product_name, p.user_id, u.username 
FROM admin_panel.products p 
LEFT JOIN admin_panel.users u ON p.user_id = u.id;
```

---

## ✅ Başarı Kontrolü

Migration başarılı olduysa:

1. `products` tablosunda `user_id` kolonu görünmelidir
2. Foreign key constraint (`fk_product_user`) oluşturulmuş olmalıdır
3. Index (`idx_products_user_id`) oluşturulmuş olmalıdır

---

## 🔄 Geri Alma (Rollback)

Eğer migration'ı geri almak isterseniz:

```sql
-- Foreign key constraint'i kaldır
ALTER TABLE admin_panel.products 
DROP CONSTRAINT IF EXISTS fk_product_user;

-- Index'i kaldır
DROP INDEX IF EXISTS admin_panel.idx_products_user_id;

-- Kolonu kaldır
ALTER TABLE admin_panel.products 
DROP COLUMN IF EXISTS user_id;
```

---

## ⚠️ Önemli Notlar

1. **Yedek Alın:** Migration çalıştırmadan önce veritabanınızın yedeğini alın
2. **Test Ortamında Deneyin:** Önce test/development ortamında deneyin
3. **Mevcut Veriler:** Eğer veritabanında ürünler varsa, bunlara kullanıcı ataması yapmanız gerekebilir
4. **Uygulama Yeniden Başlatma:** Migration sonrası Spring Boot uygulamasını yeniden başlatmanız önerilir

---

## 🐛 Sorun Giderme

### Hata: "column user_id already exists"
Kolon zaten eklenmiş. Migration'ı tekrar çalıştırmaya gerek yok.

### Hata: "constraint fk_product_user already exists"
Constraint zaten oluşturulmuş. Migration'ı tekrar çalıştırmaya gerek yok.

### Hata: "violates foreign key constraint"
Mevcut ürünlerde `user_id` değeri, `users` tablosunda olmayan bir ID'ye işaret ediyor. Önce bu ürünleri düzeltin.

