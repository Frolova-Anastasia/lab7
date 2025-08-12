ALTER TABLE organizations
DROP CONSTRAINT organizations_product_id_fkey,
ADD CONSTRAINT organizations_product_id_fkey
FOREIGN KEY (product_id) REFERENCES products(id)
ON DELETE CASCADE;
