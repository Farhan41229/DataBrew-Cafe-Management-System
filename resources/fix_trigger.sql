USE cafedb;

-- 1. Create junction table linking menu items to their ingredients
CREATE TABLE IF NOT EXISTS menu_item_ingredients (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  menu_item_id BIGINT UNSIGNED NOT NULL,
  ingredient_id BIGINT UNSIGNED NOT NULL,
  qty_needed  DECIMAL(10,2) NOT NULL DEFAULT 1.00,
  CONSTRAINT fk_mii_menu FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
  CONSTRAINT fk_mii_ing  FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
  UNIQUE KEY uq_menu_ing (menu_item_id, ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Populate sensible mappings (menu_item → ingredient, qty_needed per 1 unit sold)
INSERT IGNORE INTO menu_item_ingredients (menu_item_id, ingredient_id, qty_needed) VALUES
-- Coffee items → Coffee Beans(1), Milk(2)
(1, 1, 18),    -- Espresso: 18g coffee beans
(2, 1, 18),    -- Latte: 18g coffee beans + 200ml milk
(2, 2, 200),
(4, 1, 18),    -- Pancreas(Cappuccino variant): coffee beans + milk
(4, 2, 150),
(20, 1, 18),   -- Americano: coffee beans
(5, 1, 18),    -- Capawcino: coffee beans + milk
(5, 2, 150),

-- Bakery items → Butter(3), Flour(14), Eggs(13), Sugar(18)
(3, 3, 30),    -- Croissant: butter, flour
(3, 14, 50),

-- Tea items → Tea Leaves(19)
(6, 19, 5),    -- Green Tea: tea leaves
(7, 19, 5),    -- Masala Chai: tea leaves + milk
(7, 2, 150),

-- Smoothies → Mango Pulp(16), Blueberries(7), Milk(2)
(8, 16, 100),  -- Mango Smoothie: mango pulp + milk
(8, 2, 150),
(9, 7, 80),    -- Berry Blast: blueberries + milk
(9, 2, 150),

-- Grilled items → Chicken Breast(9), Bread Slices(8)
(10, 9, 150),  -- Chicken Sandwich: chicken + bread
(10, 8, 2),
(11, 9, 200),  -- Club Sandwich: chicken + bread
(11, 8, 3),

-- Dessert items → Chocolate Powder(10), Cream(12), Sugar(18)
(12, 10, 30),  -- Chocolate Cake: choco + cream + sugar + flour
(12, 12, 50),
(12, 18, 40),
(12, 14, 60),
(13, 10, 50),  -- Brownie: choco + butter + sugar + flour
(13, 3, 30),
(13, 18, 30),
(13, 14, 40),

-- Specials → Honey(15), Cinnamon(11), Vanilla(20), Maple Syrup(17)
(14, 15, 20),  -- Honey Latte: honey + coffee + milk
(14, 1, 18),
(14, 2, 200),
(15, 11, 5),   -- Cinnamon Roll: cinnamon + flour + butter + sugar
(15, 14, 60),
(15, 3, 25),
(15, 18, 30),
(16, 20, 10),  -- Vanilla Milkshake: vanilla + milk + cream
(16, 2, 250),
(16, 12, 50),
(17, 17, 30),  -- Maple Pancakes: maple syrup + flour + eggs
(17, 14, 80),
(17, 13, 2),
(18, 4, 2),    -- Sausage Roll: sausage rolls + flour
(18, 14, 40),
(19, 6, 3),    -- Cookie Pack: cookies + butter + sugar
(19, 3, 20),
(19, 18, 25);

-- 3. Drop the broken trigger
DROP TRIGGER IF EXISTS trg_order_items_after_insert;

-- 4. Recreate with proper JOIN through menu_item_ingredients
DELIMITER //
CREATE TRIGGER trg_order_items_after_insert
AFTER INSERT ON order_items FOR EACH ROW
BEGIN
  UPDATE inventory inv
  JOIN menu_item_ingredients mii ON mii.ingredient_id = inv.ingredient_id
  SET inv.quantity = inv.quantity - (mii.qty_needed * NEW.quantity)
  WHERE mii.menu_item_id = NEW.menu_item_id
    AND inv.quantity >= (mii.qty_needed * NEW.quantity);
END //
DELIMITER ;

SELECT 'Trigger fixed with proper ingredient mapping!' AS result;
