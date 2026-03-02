-- ============================================================
-- DataBrew Cafe Management System  –  Bulk Seed Data
-- Adds rows to bring every table to 15-20+ entries.
-- Does NOT alter or delete any existing records.
-- ============================================================

USE cafedb;

SET FOREIGN_KEY_CHECKS = 0;

-- Temporarily disable triggers that conflict with bulk inserts
DROP TRIGGER IF EXISTS trg_users_before_insert;
DROP TRIGGER IF EXISTS trg_payments_after_insert;
DROP TRIGGER IF EXISTS trg_order_items_after_insert;

-- ────────────────── USERS (existing: 6, need 14 more → 20) ──────────────────
-- password hash = SHA-256 of 'Staff@123'
SET @pw = SHA2('Staff@123', 256);
INSERT INTO users (username, email, password_hash, full_name, phone, is_active) VALUES
('barista1',  'barista1@databrew.com',  @pw, 'Maria Santos',     '01711000001', 1),
('barista2',  'barista2@databrew.com',  @pw, 'James Wilson',     '01711000002', 1),
('cashier1',  'cashier1@databrew.com',  @pw, 'Anika Rahman',     '01711000003', 1),
('cashier2',  'cashier2@databrew.com',  @pw, 'David Chen',       '01711000004', 1),
('manager1',  'manager1@databrew.com',  @pw, 'Fatima Begum',     '01711000005', 1),
('manager2',  'manager2@databrew.com',  @pw, 'Robert Miller',    '01711000006', 1),
('clerk1',   'clerk1@databrew.com',    @pw, 'Sumaiya Akter',    '01711000007', 1),
('clerk2',   'clerk2@databrew.com',    @pw, 'Tom Anderson',     '01711000008', 1),
('viewer1',  'viewer1@databrew.com',   @pw, 'Nadia Hossain',    '01711000009', 1),
('viewer2',  'viewer2@databrew.com',   @pw, 'Kevin Brown',      '01711000010', 1),
('staff1',   'staff1@databrew.com',    @pw, 'Riya Chakraborty', '01711000011', 1),
('staff2',   'staff2@databrew.com',    @pw, 'Emily Taylor',     '01711000012', 1),
('intern1',  'intern1@databrew.com',   @pw, 'Arif Hasan',       '01711000013', 0),
('intern2',  'intern2@databrew.com',   @pw, 'Sophie Martin',    '01711000014', 0);

-- ────────────────── USER_ROLES (assign roles to new users) ──────────────────
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username='barista1'  AND r.name='STAFF'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='barista2'  AND r.name='STAFF'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='cashier1'  AND r.name='CASHIER'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='cashier2'  AND r.name='CASHIER'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='manager1'  AND r.name='MANAGER'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='manager2'  AND r.name='MANAGER'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='clerk1'    AND r.name='INVENTORY_CLERK'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='clerk2'    AND r.name='INVENTORY_CLERK'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='viewer1'   AND r.name='VIEWER'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='viewer2'   AND r.name='VIEWER'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='staff1'    AND r.name='STAFF'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='staff2'    AND r.name='STAFF'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='intern1'   AND r.name='VIEWER'
UNION ALL SELECT u.id, r.id FROM users u, roles r WHERE u.username='intern2'   AND r.name='VIEWER';

-- ────────────────── EMPLOYEES (existing: 2, need 18 more → 20) ──────────────────
INSERT INTO employees (position, full_name, branch, age, status, shift_id, hire_date, salary, bank_account) VALUES
('Barista',           'Maria Santos',       'Motijheel', 26, 'Active',   1, '2025-06-15', 22000.00, 'DB-1001'),
('Barista',           'James Wilson',       'Mdpur',     29, 'Active',   2, '2025-07-01', 21000.00, 'DB-1002'),
('Cashier',           'Anika Rahman',       'Motijheel', 24, 'Active',   1, '2025-08-10', 20000.00, 'DB-1003'),
('Cashier',           'David Chen',         'Mdpur',     31, 'Active',   2, '2025-09-01', 20000.00, 'DB-1004'),
('Manager',           'Fatima Begum',       'Gulshan',   38, 'Active',   1, '2024-01-15', 45000.00, 'DB-1005'),
('Manager',           'Robert Miller',      'Banani',    42, 'Active',   2, '2024-03-01', 48000.00, 'DB-1006'),
('Inventory Clerk',   'Sumaiya Akter',      'Motijheel', 27, 'Active',   1, '2025-10-01', 18000.00, 'DB-1007'),
('Inventory Clerk',   'Tom Anderson',       'Mdpur',     33, 'Active',   3, '2025-11-15', 18500.00, 'DB-1008'),
('Chef',              'Kamal Hossain',      'Gulshan',   35, 'Active',   1, '2025-04-01', 32000.00, 'DB-1009'),
('Chef',              'Sophie Martin',      'Banani',    30, 'Active',   2, '2025-05-20', 30000.00, 'DB-1010'),
('Waiter',            'Rahim Uddin',        'Motijheel', 22, 'Active',   2, '2025-12-01', 15000.00, 'DB-1011'),
('Waiter',            'Emily Taylor',       'Mdpur',     23, 'Active',   1, '2026-01-10', 15000.00, 'DB-1012'),
('Cleaner',           'Arif Hasan',         'Gulshan',   20, 'On Leave', 3, '2026-01-15', 12000.00, 'DB-1013'),
('Cleaner',           'Kevin Brown',        'Banani',    21, 'Active',   3, '2026-02-01', 12000.00, 'DB-1014'),
('Barista',           'Riya Chakraborty',   'Gulshan',   25, 'Active',   1, '2025-03-10', 23000.00, 'DB-1015'),
('Security',          'Nadia Hossain',      'Motijheel', 40, 'Active',   3, '2024-06-01', 16000.00, 'DB-1016'),
('Barista',           'Tanvir Ahmed',       'Banani',    28, 'Active',   2, '2025-09-15', 22500.00, 'DB-1017'),
('Delivery Rider',    'Rafiq Islam',        'Mdpur',     32, 'Inactive', 2, '2025-02-01', 14000.00, 'DB-1018');

-- ────────────────── SHIFTS (existing: 3, need 17 more → 20) ──────────────────
INSERT INTO shifts (name, start_time, end_time) VALUES
('Early Morning',  '04:00:00', '08:00:00'),
('Late Morning',   '08:00:00', '12:00:00'),
('Afternoon',      '12:00:00', '16:00:00'),
('Evening',        '16:00:00', '20:00:00'),
('Late Night',     '20:00:00', '00:00:00'),
('Weekend Morning','07:00:00', '13:00:00'),
('Weekend Evening','13:00:00', '21:00:00'),
('Split AM',       '06:00:00', '10:00:00'),
('Split PM',       '15:00:00', '19:00:00'),
('Holiday',        '09:00:00', '17:00:00'),
('Overtime AM',    '05:00:00', '09:00:00'),
('Overtime PM',    '21:00:00', '01:00:00'),
('Double Morning', '06:00:00', '18:00:00'),
('Double Night',   '18:00:00', '06:00:00'),
('Flexible',       '10:00:00', '16:00:00'),
('Part-time AM',   '08:00:00', '12:00:00'),
('Part-time PM',   '13:00:00', '17:00:00');

-- ────────────────── CATEGORIES (existing: 2, need 18 more → 20) ──────────────────
INSERT INTO categories (name, description) VALUES
('Tea',            'All varieties of tea'),
('Smoothies',      'Fresh fruit smoothies'),
('Pastries',       'Cakes, muffins, and pastries'),
('Sandwiches',     'Hot and cold sandwiches'),
('Desserts',       'Sweet treats and desserts'),
('Juices',         'Fresh-pressed juices'),
('Breakfast',      'Morning breakfast combos'),
('Lunch Specials', 'Afternoon lunch items'),
('Snacks',         'Light bites and chips'),
('Milkshakes',     'Blended milkshakes'),
('Iced Drinks',    'Cold and iced beverages'),
('Hot Chocolate',  'Chocolate-based hot drinks'),
('Wraps',          'Tortilla wraps'),
('Salads',         'Fresh salads'),
('Soups',          'Warm soups'),
('Waffles',        'Belgian and classic waffles'),
('Bagels',         'Bagels with toppings'),
('Specials',       'Chef specials of the day');

-- ────────────────── MENU_ITEMS (existing: 5, need 15 more → 20) ──────────────────
-- We'll use category IDs dynamically from the names inserted above
INSERT INTO menu_items (category_id, name, description, price, is_active) VALUES
((SELECT id FROM categories WHERE name='Tea'),            'Green Tea',           'Organic green tea',           2.50, 1),
((SELECT id FROM categories WHERE name='Tea'),            'Masala Chai',         'Spiced Indian chai',          3.00, 1),
((SELECT id FROM categories WHERE name='Smoothies'),      'Mango Smoothie',      'Fresh mango blended',         5.00, 1),
((SELECT id FROM categories WHERE name='Smoothies'),      'Berry Blast',         'Mixed berry smoothie',        5.50, 1),
((SELECT id FROM categories WHERE name='Pastries'),       'Blueberry Muffin',    'Fresh-baked muffin',          3.20, 1),
((SELECT id FROM categories WHERE name='Sandwiches'),     'Club Sandwich',       'Triple-decker classic',       6.50, 1),
((SELECT id FROM categories WHERE name='Sandwiches'),     'Grilled Cheese',      'Toasted cheese sandwich',     4.50, 1),
((SELECT id FROM categories WHERE name='Desserts'),       'Tiramisu',            'Italian coffee dessert',      5.80, 1),
((SELECT id FROM categories WHERE name='Juices'),         'Orange Juice',        'Freshly squeezed',            3.50, 1),
((SELECT id FROM categories WHERE name='Breakfast'),      'Pancake Stack',       'Maple syrup pancakes',        6.00, 1),
((SELECT id FROM categories WHERE name='Milkshakes'),     'Chocolate Milkshake', 'Rich chocolate shake',        4.80, 1),
((SELECT id FROM categories WHERE name='Hot Chocolate'),  'Classic Hot Cocoa',   'Warm chocolate drink',        3.80, 1),
((SELECT id FROM categories WHERE name='Wraps'),          'Chicken Wrap',        'Grilled chicken tortilla',    5.50, 1),
((SELECT id FROM categories WHERE name='Waffles'),        'Belgian Waffle',      'Crispy waffle with cream',    5.20, 1),
((SELECT id FROM categories WHERE name='Coffee'),         'Americano',           'Classic black coffee',        3.00, 1);

-- ────────────────── INGREDIENTS (existing: 6, need 14 more → 20) ──────────────────
INSERT INTO ingredients (name, unit, min_threshold) VALUES
('Sugar',           'g',     500.00),
('Flour',           'g',     800.00),
('Eggs',            'pcs',   100.00),
('Cream',           'ml',    500.00),
('Chocolate Powder','g',     300.00),
('Tea Leaves',      'g',     400.00),
('Honey',           'ml',    200.00),
('Vanilla Extract', 'ml',    100.00),
('Cinnamon',        'g',      50.00),
('Blueberries',     'g',     200.00),
('Mango Pulp',      'ml',    500.00),
('Chicken Breast',  'g',     600.00),
('Bread Slices',    'pcs',   100.00),
('Maple Syrup',     'ml',    300.00);

-- ────────────────── INVENTORY (add stock for new ingredients) ──────────────────
INSERT INTO inventory (ingredient_id, quantity)
SELECT id, CASE
    WHEN name='Sugar'           THEN 3000
    WHEN name='Flour'           THEN 5000
    WHEN name='Eggs'            THEN 240
    WHEN name='Cream'           THEN 2000
    WHEN name='Chocolate Powder'THEN 1500
    WHEN name='Tea Leaves'      THEN 2000
    WHEN name='Honey'           THEN 800
    WHEN name='Vanilla Extract' THEN 350
    WHEN name='Cinnamon'        THEN 200
    WHEN name='Blueberries'     THEN 1000
    WHEN name='Mango Pulp'      THEN 2500
    WHEN name='Chicken Breast'  THEN 3000
    WHEN name='Bread Slices'    THEN 200
    WHEN name='Maple Syrup'     THEN 1200
    ELSE 500
END
FROM ingredients
WHERE id NOT IN (SELECT ingredient_id FROM inventory);

-- ────────────────── SUPPLIERS (existing: 2, need 18 more → 20) ──────────────────
INSERT INTO suppliers (name, contact, phone, email) VALUES
('Golden Harvest',    'Mr. Karim',      '01812345001', 'karim@goldenharvest.com'),
('Fresh Fields',      'Ms. Nazma',      '01812345002', 'nazma@freshfields.com'),
('City Dairy',        'Mr. Habib',      '01812345003', 'habib@citydairy.com'),
('Tea Garden Ltd',    'Mrs. Sultana',   '01812345004', 'sultana@teagarden.com'),
('Berry Farms',       'Mr. Pavel',      '01812345005', 'pavel@berryfarms.com'),
('Sugar Refinery Co', 'Mr. Alam',       '01812345006', 'alam@sugarrefinery.com'),
('Flour Mills Inc',   'Ms. Rina',       '01812345007', 'rina@flourmills.com'),
('Egg Zone',          'Mr. Manik',      '01812345008', 'manik@eggzone.com'),
('Chocolate World',   'Ms. Luna',       '01812345009', 'luna@chocoworld.com'),
('Spice Traders',     'Mr. Rashid',     '01812345010', 'rashid@spicetraders.com'),
('Honey Bee Farm',    'Mrs. Afroza',    '01812345011', 'afroza@honeybeefarm.com'),
('Poultry Plus',      'Mr. Farhan',     '01812345012', 'farhan@poultryplus.com'),
('Bakery Supplies',   'Ms. Sharmin',    '01812345013', 'sharmin@bakerysupplies.com'),
('Tropical Fruits',   'Mr. Jamal',      '01812345014', 'jamal@tropicalfruits.com'),
('Organic Hub',       'Mrs. Laboni',    '01812345015', 'laboni@organichub.com'),
('Metro Wholesale',   'Mr. Sohel',      '01812345016', 'sohel@metrowholesale.com'),
('Sunrise Agro',      'Ms. Tania',      '01812345017', 'tania@sunriseagro.com'),
('Global Foods',      'Mr. Imran',      '01812345018', 'imran@globalfoods.com');

-- ────────────────── DISCOUNTS (existing: 4, need 16 more → 20) ──────────────────
INSERT INTO discounts (name, type, value, applies_to) VALUES
('Senior Citizen 12%',    'PERCENT', 12.00, 'GENERAL'),
('Birthday 25%',          'PERCENT', 25.00, 'GENERAL'),
('Happy Hour 15%',        'PERCENT', 15.00, 'GENERAL'),
('Weekend Special 10%',   'PERCENT', 10.00, 'GENERAL'),
('New Customer 5%',       'PERCENT',  5.00, 'GENERAL'),
('Flat $3 Off',           'FLAT',     3.00, 'GENERAL'),
('Flat $10 Off',          'FLAT',    10.00, 'LOYAL'),
('Staff Family 20%',      'PERCENT', 20.00, 'STAFF'),
('Student Combo 8%',      'PERCENT',  8.00, 'STUDENT'),
('Early Bird 7%',         'PERCENT',  7.00, 'GENERAL'),
('Loyalty Gold 18%',      'PERCENT', 18.00, 'LOYAL'),
('Festive Offer 30%',     'PERCENT', 30.00, 'GENERAL'),
('Flat $2 Off',           'FLAT',     2.00, 'STUDENT'),
('Corporate 15%',         'PERCENT', 15.00, 'GENERAL'),
('Summer Sale 22%',       'PERCENT', 22.00, 'GENERAL'),
('Ramadan Special 20%',   'PERCENT', 20.00, 'GENERAL');

-- ────────────────── TAXES (existing: 2, need 18 more → 20) ──────────────────
INSERT INTO taxes (name, rate) VALUES
('Service Charge',       5.00),
('City Tax',             3.00),
('Environmental Levy',   1.50),
('Tourism Tax',          2.00),
('Health Surcharge',     1.00),
('Municipal Tax',        2.50),
('State Tax',            4.00),
('Federal Tax',          6.00),
('Luxury Tax',           8.00),
('Import Duty',          7.50),
('Excise Tax',           3.50),
('Sales Tax',           12.00),
('Beverage Tax',         2.00),
('Food Tax',             1.50),
('Packaging Fee',        0.50),
('Digital Service Tax',  3.00),
('Carbon Tax',           1.00),
('Regional Surcharge',   2.25);

-- ────────────────── PURCHASES (existing: 2, need 18 more → 20) ──────────────────
-- Use supplier IDs 1-4 (first 4 suppliers) and ingredient IDs for variety
INSERT INTO purchases (supplier_id, ingredient_id, quantity, cost, purchased_at) VALUES
(1, 1, 2000.00,  1800.00, '2026-01-05 09:30:00'),
(2, 2, 5000.00,  4500.00, '2026-01-08 10:00:00'),
(1, 3,  500.00,   600.00, '2026-01-12 11:15:00'),
(2, 4,   50.00,   150.00, '2026-01-15 08:45:00'),
(1, 5,  200.00,   350.00, '2026-01-18 14:00:00'),
(2, 6,  300.00,   250.00, '2026-01-22 09:00:00'),
((SELECT id FROM suppliers WHERE name='Golden Harvest'), 7,  3000.00,  2500.00, '2026-01-25 10:30:00'),
((SELECT id FROM suppliers WHERE name='Fresh Fields'),   8,  4000.00,  3800.00, '2026-01-28 11:00:00'),
((SELECT id FROM suppliers WHERE name='City Dairy'),     9,   150.00,   300.00, '2026-02-01 09:15:00'),
((SELECT id FROM suppliers WHERE name='City Dairy'),    10,  1500.00,  1200.00, '2026-02-04 10:45:00'),
((SELECT id FROM suppliers WHERE name='Tea Garden Ltd'), 11,   400.00,   450.00, '2026-02-07 08:30:00'),
((SELECT id FROM suppliers WHERE name='Berry Farms'),   12,   800.00,   900.00, '2026-02-10 13:00:00'),
((SELECT id FROM suppliers WHERE name='Sugar Refinery Co'),13, 2000.00,  1600.00, '2026-02-13 09:00:00'),
((SELECT id FROM suppliers WHERE name='Flour Mills Inc'),14,  1000.00,  1100.00, '2026-02-16 10:00:00'),
((SELECT id FROM suppliers WHERE name='Egg Zone'),      15,   500.00,   700.00, '2026-02-19 11:30:00'),
((SELECT id FROM suppliers WHERE name='Chocolate World'),16,   600.00,   850.00, '2026-02-22 14:00:00'),
((SELECT id FROM suppliers WHERE name='Spice Traders'), 17,   200.00,   180.00, '2026-02-25 09:45:00'),
((SELECT id FROM suppliers WHERE name='Honey Bee Farm'),18,   350.00,   500.00, '2026-02-28 10:15:00');

-- ────────────────── ORDERS (existing: 2, need 18 more → 20) ──────────────────
INSERT INTO orders (customer_name, customer_type, status, discount_id, tax_id, subtotal, tax_amount, discount_amount, total, created_at) VALUES
('Alice Johnson',   'GENERAL', 'PAID',      NULL, NULL,  12.00,  0.00,  0.00, 12.00, '2026-02-15 08:30:00'),
('Bob Smith',       'STUDENT', 'PAID',         1,    1,  18.50,  2.50,  1.85, 19.15, '2026-02-15 09:15:00'),
('Charlie Davis',   'LOYAL',   'PAID',         3,    1,  25.00,  3.38,  5.00, 23.38, '2026-02-16 10:00:00'),
('Diana Prince',    'GENERAL', 'PAID',      NULL,    2,  15.80,  1.58,  0.00, 17.38, '2026-02-16 11:30:00'),
('Edward Norton',   'STAFF',   'PAID',         2, NULL,  22.00,  0.00,  3.30, 18.70, '2026-02-17 07:45:00'),
('Fiona Green',     'GENERAL', 'PAID',         4, NULL,   8.50,  0.00,  0.00,  3.50, '2026-02-17 12:00:00'),
('George Harris',   'LOYAL',   'PAID',         3,    1,  30.00,  4.05,  6.00, 28.05, '2026-02-18 08:00:00'),
('Hannah Lee',      'STUDENT', 'PAID',         1, NULL,  10.00,  0.00,  1.00,  9.00, '2026-02-18 14:30:00'),
('Ivan Petrov',     'GENERAL', 'CANCELLED', NULL, NULL,  20.00,  0.00,  0.00, 20.00, '2026-02-19 09:00:00'),
('Julia White',     'GENERAL', 'PAID',      NULL,    2,  16.50,  1.65,  0.00, 18.15, '2026-02-19 15:00:00'),
('Karl Fischer',    'STAFF',   'PAID',         2,    1,  28.00,  3.78,  4.20, 27.58, '2026-02-20 08:30:00'),
('Lara Croft',      'GENERAL', 'PAID',      NULL, NULL,   9.00,  0.00,  0.00,  9.00, '2026-02-20 13:00:00'),
('Mike Tyson',      'LOYAL',   'PAID',         3,    2,  35.00,  3.15,  7.00, 31.15, '2026-02-21 10:00:00'),
('Nina Simone',     'GENERAL', 'PENDING',   NULL, NULL,  14.00,  0.00,  0.00, 14.00, '2026-02-21 16:45:00'),
('Oscar Wilde',     'STUDENT', 'PAID',         1, NULL,  11.30,  0.00,  1.13, 10.17, '2026-02-22 09:30:00'),
('Priya Sharma',    'GENERAL', 'PAID',      NULL,    1,  19.00,  2.85,  0.00, 21.85, '2026-02-22 11:00:00'),
('Quinn Hughes',    'GENERAL', 'PAID',         4, NULL,  13.50,  0.00,  0.00,  8.50, '2026-02-23 08:15:00'),
('Rachel Adams',    'LOYAL',   'PAID',         3,    2,  27.60,  2.48,  5.52, 24.56, '2026-02-23 14:00:00');

-- ────────────────── ORDER_ITEMS (for the 18 new orders above) ──────────────────
-- We'll reference the orders by their created_at timestamps to get their IDs
INSERT INTO order_items (order_id, menu_item_id, quantity, unit_price, line_total) VALUES
-- Order 3: Alice - Espresso x2 + Croissant x2
((SELECT id FROM orders WHERE customer_name='Alice Johnson' LIMIT 1), 1, 2, 3.50, 7.00),
((SELECT id FROM orders WHERE customer_name='Alice Johnson' LIMIT 1), 3, 2, 2.80, 5.00),
-- Order 4: Bob - Latte + Croissant + Espresso
((SELECT id FROM orders WHERE customer_name='Bob Smith' LIMIT 1), 2, 2, 4.50, 9.00),
((SELECT id FROM orders WHERE customer_name='Bob Smith' LIMIT 1), 3, 1, 2.80, 2.80),
((SELECT id FROM orders WHERE customer_name='Bob Smith' LIMIT 1), 1, 2, 3.50, 7.00),
-- Order 5: Charlie
((SELECT id FROM orders WHERE customer_name='Charlie Davis' LIMIT 1), 1, 3, 3.50, 10.50),
((SELECT id FROM orders WHERE customer_name='Charlie Davis' LIMIT 1), 2, 2, 4.50, 9.00),
((SELECT id FROM orders WHERE customer_name='Charlie Davis' LIMIT 1), 3, 2, 2.80, 5.60),
-- Order 6: Diana
((SELECT id FROM orders WHERE customer_name='Diana Prince' LIMIT 1), 2, 2, 4.50, 9.00),
((SELECT id FROM orders WHERE customer_name='Diana Prince' LIMIT 1), 3, 1, 2.80, 2.80),
((SELECT id FROM orders WHERE customer_name='Diana Prince' LIMIT 1), 1, 1, 3.50, 3.50),
-- Order 7: Edward
((SELECT id FROM orders WHERE customer_name='Edward Norton' LIMIT 1), 1, 4, 3.50, 14.00),
((SELECT id FROM orders WHERE customer_name='Edward Norton' LIMIT 1), 3, 3, 2.80, 8.40),
-- Order 8: Fiona
((SELECT id FROM orders WHERE customer_name='Fiona Green' LIMIT 1), 3, 1, 2.80, 2.80),
((SELECT id FROM orders WHERE customer_name='Fiona Green' LIMIT 1), 1, 1, 3.50, 3.50),
-- Order 9: George
((SELECT id FROM orders WHERE customer_name='George Harris' LIMIT 1), 2, 4, 4.50, 18.00),
((SELECT id FROM orders WHERE customer_name='George Harris' LIMIT 1), 3, 3, 2.80, 8.40),
((SELECT id FROM orders WHERE customer_name='George Harris' LIMIT 1), 1, 1, 3.50, 3.50),
-- Order 10: Hannah
((SELECT id FROM orders WHERE customer_name='Hannah Lee' LIMIT 1), 1, 2, 3.50, 7.00),
((SELECT id FROM orders WHERE customer_name='Hannah Lee' LIMIT 1), 3, 1, 2.80, 2.80),
-- Order 11: Ivan (cancelled)
((SELECT id FROM orders WHERE customer_name='Ivan Petrov' LIMIT 1), 2, 3, 4.50, 13.50),
((SELECT id FROM orders WHERE customer_name='Ivan Petrov' LIMIT 1), 1, 2, 3.50, 7.00),
-- Order 12: Julia
((SELECT id FROM orders WHERE customer_name='Julia White' LIMIT 1), 1, 3, 3.50, 10.50),
((SELECT id FROM orders WHERE customer_name='Julia White' LIMIT 1), 3, 2, 2.80, 5.60),
-- Order 13: Karl
((SELECT id FROM orders WHERE customer_name='Karl Fischer' LIMIT 1), 2, 4, 4.50, 18.00),
((SELECT id FROM orders WHERE customer_name='Karl Fischer' LIMIT 1), 1, 3, 3.50, 10.50),
-- Order 14: Lara
((SELECT id FROM orders WHERE customer_name='Lara Croft' LIMIT 1), 2, 2, 4.50, 9.00),
-- Order 15: Mike
((SELECT id FROM orders WHERE customer_name='Mike Tyson' LIMIT 1), 1, 5, 3.50, 17.50),
((SELECT id FROM orders WHERE customer_name='Mike Tyson' LIMIT 1), 2, 3, 4.50, 13.50),
((SELECT id FROM orders WHERE customer_name='Mike Tyson' LIMIT 1), 3, 2, 2.80, 5.60),
-- Order 16: Nina
((SELECT id FROM orders WHERE customer_name='Nina Simone' LIMIT 1), 1, 4, 3.50, 14.00),
-- Order 17: Oscar
((SELECT id FROM orders WHERE customer_name='Oscar Wilde' LIMIT 1), 2, 1, 4.50, 4.50),
((SELECT id FROM orders WHERE customer_name='Oscar Wilde' LIMIT 1), 3, 1, 2.80, 2.80),
((SELECT id FROM orders WHERE customer_name='Oscar Wilde' LIMIT 1), 1, 1, 3.50, 3.50),
-- Order 18: Priya
((SELECT id FROM orders WHERE customer_name='Priya Sharma' LIMIT 1), 1, 3, 3.50, 10.50),
((SELECT id FROM orders WHERE customer_name='Priya Sharma' LIMIT 1), 2, 2, 4.50, 9.00),
-- Order 19: Quinn
((SELECT id FROM orders WHERE customer_name='Quinn Hughes' LIMIT 1), 3, 3, 2.80, 8.40),
((SELECT id FROM orders WHERE customer_name='Quinn Hughes' LIMIT 1), 1, 1, 3.50, 3.50),
-- Order 20: Rachel
((SELECT id FROM orders WHERE customer_name='Rachel Adams' LIMIT 1), 2, 4, 4.50, 18.00),
((SELECT id FROM orders WHERE customer_name='Rachel Adams' LIMIT 1), 3, 3, 2.80, 8.40),
((SELECT id FROM orders WHERE customer_name='Rachel Adams' LIMIT 1), 1, 1, 3.50, 3.50);

-- ────────────────── PAYMENTS (for PAID orders — 16 of the 18 new orders) ──────────────────
INSERT INTO payments (order_id, amount, method, paid_at, reference) VALUES
((SELECT id FROM orders WHERE customer_name='Alice Johnson' LIMIT 1), 12.00, 'CASH', '2026-02-15 08:35:00', NULL),
((SELECT id FROM orders WHERE customer_name='Bob Smith' LIMIT 1),     19.15, 'CARD', '2026-02-15 09:20:00', 'VISA-4821'),
((SELECT id FROM orders WHERE customer_name='Charlie Davis' LIMIT 1), 23.38, 'MFS',  '2026-02-16 10:05:00', 'bKash-7712'),
((SELECT id FROM orders WHERE customer_name='Diana Prince' LIMIT 1),  17.38, 'CARD', '2026-02-16 11:35:00', 'MC-3301'),
((SELECT id FROM orders WHERE customer_name='Edward Norton' LIMIT 1), 18.70, 'CASH', '2026-02-17 07:50:00', NULL),
((SELECT id FROM orders WHERE customer_name='Fiona Green' LIMIT 1),    3.50, 'CASH', '2026-02-17 12:05:00', NULL),
((SELECT id FROM orders WHERE customer_name='George Harris' LIMIT 1), 28.05, 'CARD', '2026-02-18 08:08:00', 'VISA-6655'),
((SELECT id FROM orders WHERE customer_name='Hannah Lee' LIMIT 1),     9.00, 'MFS',  '2026-02-18 14:35:00', 'Nagad-9901'),
((SELECT id FROM orders WHERE customer_name='Julia White' LIMIT 1),   18.15, 'CASH', '2026-02-19 15:05:00', NULL),
((SELECT id FROM orders WHERE customer_name='Karl Fischer' LIMIT 1),  27.58, 'CARD', '2026-02-20 08:35:00', 'AMEX-1122'),
((SELECT id FROM orders WHERE customer_name='Lara Croft' LIMIT 1),     9.00, 'CASH', '2026-02-20 13:05:00', NULL),
((SELECT id FROM orders WHERE customer_name='Mike Tyson' LIMIT 1),    31.15, 'MFS',  '2026-02-21 10:08:00', 'bKash-4455'),
((SELECT id FROM orders WHERE customer_name='Oscar Wilde' LIMIT 1),   10.17, 'CASH', '2026-02-22 09:35:00', NULL),
((SELECT id FROM orders WHERE customer_name='Priya Sharma' LIMIT 1),  21.85, 'CARD', '2026-02-22 11:05:00', 'VISA-8899'),
((SELECT id FROM orders WHERE customer_name='Quinn Hughes' LIMIT 1),    8.50, 'CASH', '2026-02-23 08:20:00', NULL),
((SELECT id FROM orders WHERE customer_name='Rachel Adams' LIMIT 1),  24.56, 'CARD', '2026-02-23 14:08:00', 'MC-7766');

-- ────────────────── INVOICES (for all PAID orders) ──────────────────
INSERT INTO invoices (order_id, invoice_number, payment_id, total, issued_at)
SELECT o.id,
       CONCAT('INV-', DATE_FORMAT(o.created_at, '%Y%m%d'), '-', LPAD(ROW_NUMBER() OVER (ORDER BY o.id) + 2, 6, '0')),
       p.id,
       p.amount,
       p.paid_at
FROM orders o
JOIN payments p ON p.order_id = o.id
WHERE o.id NOT IN (SELECT order_id FROM invoices)
  AND o.status = 'PAID';

-- ────────────────── ATTENDANCE (existing: 0, need 20) ──────────────────
-- Use employee IDs 1,2 (existing) + some new employees (IDs 3-20)
INSERT INTO attendance (employee_id, shift_id, work_date, check_in, check_out, status) VALUES
(1, 1, '2026-02-15', '2026-02-15 06:02:00', '2026-02-15 14:05:00', 'PRESENT'),
(2, 2, '2026-02-15', '2026-02-15 14:10:00', '2026-02-15 22:00:00', 'PRESENT'),
(1, 1, '2026-02-16', '2026-02-16 06:00:00', '2026-02-16 14:00:00', 'PRESENT'),
(2, 2, '2026-02-16', '2026-02-16 14:30:00', '2026-02-16 22:00:00', 'LATE'),
(1, 1, '2026-02-17', NULL, NULL, 'ABSENT'),
(2, 2, '2026-02-17', '2026-02-17 14:05:00', '2026-02-17 22:10:00', 'PRESENT'),
(1, 1, '2026-02-18', '2026-02-18 06:00:00', '2026-02-18 14:00:00', 'PRESENT'),
(2, 2, '2026-02-18', '2026-02-18 14:00:00', '2026-02-18 22:00:00', 'PRESENT'),
(1, 1, '2026-02-19', '2026-02-19 06:15:00', '2026-02-19 14:00:00', 'LATE'),
(2, 2, '2026-02-19', '2026-02-19 14:00:00', '2026-02-19 22:00:00', 'PRESENT'),
(3, 1, '2026-02-20', '2026-02-20 06:00:00', '2026-02-20 14:00:00', 'PRESENT'),
(4, 2, '2026-02-20', '2026-02-20 14:00:00', '2026-02-20 22:00:00', 'PRESENT'),
(5, 1, '2026-02-21', '2026-02-21 06:05:00', '2026-02-21 14:00:00', 'PRESENT'),
(6, 2, '2026-02-21', '2026-02-21 14:20:00', '2026-02-21 22:00:00', 'LATE'),
(7, 1, '2026-02-22', '2026-02-22 06:00:00', '2026-02-22 14:00:00', 'PRESENT'),
(8, 3, '2026-02-22', '2026-02-22 22:00:00', '2026-02-23 06:00:00', 'PRESENT'),
(9, 1, '2026-02-23', '2026-02-23 06:00:00', '2026-02-23 14:00:00', 'PRESENT'),
(10, 2, '2026-02-23', NULL, NULL, 'ABSENT'),
(11, 2, '2026-02-24', '2026-02-24 14:00:00', '2026-02-24 22:00:00', 'PRESENT'),
(12, 1, '2026-02-24', '2026-02-24 06:00:00', '2026-02-24 14:00:00', 'PRESENT');

-- ────────────────── AUDIT_LOGS (existing: 28, already above 20 — add a few more for variety) ──────────────────
INSERT INTO audit_logs (user_id, action, entity, entity_id, details, created_at) VALUES
(1, 'CREATE', 'menu_items', 6, 'Added Green Tea to menu',                  '2026-02-15 08:00:00'),
(1, 'CREATE', 'menu_items', 7, 'Added Masala Chai to menu',                '2026-02-15 08:05:00'),
(1, 'CREATE', 'employees',  3, 'Hired Maria Santos as Barista',            '2026-02-15 09:00:00'),
(1, 'UPDATE', 'inventory',  1, 'Restocked Coffee Beans +2000g',            '2026-02-16 10:00:00'),
(2, 'CREATE', 'orders',     3, 'New order for Alice Johnson',              '2026-02-16 10:30:00'),
(1, 'DELETE', 'discounts',  NULL, 'Removed expired promo',                 '2026-02-17 11:00:00'),
(3, 'UPDATE', 'employees',  5, 'Updated Fatima salary to 45000',           '2026-02-18 09:00:00'),
(1, 'CREATE', 'suppliers',  3, 'Added Golden Harvest supplier',            '2026-02-18 10:00:00'),
(1, 'CREATE', 'purchases',  3, 'Purchase from Square: Coffee Beans 2000g', '2026-02-19 09:30:00'),
(2, 'UPDATE', 'menu_items', 1, 'Updated Espresso price to $3.50',          '2026-02-20 08:00:00'),
(1, 'CREATE', 'shifts',     4, 'Created Early Morning shift',              '2026-02-20 09:00:00'),
(1, 'UPDATE', 'users',      7, 'Activated barista1 account',               '2026-02-21 10:00:00');

SET FOREIGN_KEY_CHECKS = 1;

-- Re-create the triggers that were temporarily dropped
DELIMITER //

CREATE TRIGGER trg_users_before_insert
BEFORE INSERT ON users FOR EACH ROW
BEGIN
  IF CHAR_LENGTH(NEW.password_hash) < 20 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Password hash invalid';
  END IF;
  INSERT INTO audit_logs (user_id, action, entity, entity_id, details)
  VALUES (NEW.id, 'CREATE_USER', 'users', NULL, CONCAT('Created user ', NEW.username));
END //

CREATE TRIGGER trg_payments_after_insert
AFTER INSERT ON payments FOR EACH ROW
BEGIN
  UPDATE orders SET status = 'PAID' WHERE id = NEW.order_id;
  INSERT INTO audit_logs (user_id, action, entity, entity_id, details)
  VALUES (NULL, 'PAYMENT', 'orders', NEW.order_id, CONCAT('Payment ', NEW.id, ' recorded'));
END //

CREATE TRIGGER trg_order_items_after_insert
AFTER INSERT ON order_items FOR EACH ROW
BEGIN
  UPDATE inventory inv
  JOIN menu_items mi ON mi.id = NEW.menu_item_id
  JOIN ingredients ing ON ing.id = inv.ingredient_id
  SET inv.quantity = inv.quantity - NEW.quantity
  WHERE mi.id = NEW.menu_item_id;
END //

DELIMITER ;

SELECT 'Seed data inserted successfully!' AS result;
