-- 1. Добавляем колонку для хранения позиции
ALTER TABLE products ADD COLUMN position_index INTEGER;

-- 2. Заполняем позиции для уже существующих продуктов
--    Нумеруем их по creation_date, потом по id (чтобы порядок был стабильным)
WITH numbered AS (
  SELECT id, ROW_NUMBER() OVER (ORDER BY creation_date, id) AS rn
  FROM products
)
UPDATE products
SET position_index = numbered.rn
FROM numbered
WHERE products.id = numbered.id;

-- 3. Делаем поле обязательным (NOT NULL)
ALTER TABLE products ALTER COLUMN position_index SET NOT NULL;

-- 4. Добавляем индекс для быстрого поиска и сортировки по position_index
CREATE INDEX idx_products_position_index ON products(position_index);
