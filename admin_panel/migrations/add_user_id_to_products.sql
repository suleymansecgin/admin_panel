-- Migration: products tablosuna user_id kolonu ekleme
-- Tarih: 2024
-- Açıklama: Her ürünün bir kullanıcıya ait olması için user_id foreign key kolonu ekleniyor

-- Önce user_id kolonunu ekle (nullable olarak, mevcut veriler için)
ALTER TABLE admin_panel.products 
ADD COLUMN user_id BIGINT;

-- Mevcut ürünler için varsayılan bir kullanıcı ataması yapılabilir (opsiyonel)
-- Eğer users tablosunda bir admin kullanıcısı varsa:
-- UPDATE admin_panel.products 
-- SET user_id = (SELECT id FROM admin_panel.users WHERE role = 'ADMIN' LIMIT 1)
-- WHERE user_id IS NULL;

-- Foreign key constraint ekle
ALTER TABLE admin_panel.products 
ADD CONSTRAINT fk_product_user 
FOREIGN KEY (user_id) 
REFERENCES admin_panel.users(id) 
ON DELETE CASCADE;

-- Kolonu NOT NULL yap (yeni ürünler için zorunlu olacak)
-- ÖNEMLİ: Önce yukarıdaki UPDATE komutunu çalıştırdıysanız bu satırı aktif edin
-- ALTER TABLE admin_panel.products 
-- ALTER COLUMN user_id SET NOT NULL;

-- Index ekle (performans için)
CREATE INDEX idx_products_user_id ON admin_panel.products(user_id);

