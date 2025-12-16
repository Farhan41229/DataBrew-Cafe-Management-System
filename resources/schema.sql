-- DataBrew Cafe Management System - MySQL schema
DROP DATABASE IF EXISTS cafedb;
CREATE DATABASE cafedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cafedb;

-- Reference tables
CREATE TABLE roles (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255)
);

CREATE TABLE users (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash CHAR(64) NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  phone VARCHAR(20),
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CHECK (is_active IN (0,1))
);

CREATE TABLE user_roles (
  user_id BIGINT UNSIGNED NOT NULL,
  role_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE employees (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED UNIQUE,
  position VARCHAR(80) NOT NULL,
  hire_date DATE NOT NULL,
  salary DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_employees_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE shifts (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL UNIQUE,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL
);

CREATE TABLE attendance (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT UNSIGNED NOT NULL,
  shift_id BIGINT UNSIGNED NOT NULL,
  work_date DATE NOT NULL,
  check_in DATETIME,
  check_out DATETIME,
  status ENUM('PRESENT','ABSENT','LATE') NOT NULL DEFAULT 'PRESENT',
  CONSTRAINT fk_att_emp FOREIGN KEY (employee_id) REFERENCES employees(id),
  CONSTRAINT fk_att_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
  UNIQUE (employee_id, work_date)
);

CREATE TABLE categories (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL UNIQUE,
  description VARCHAR(255)
);

CREATE TABLE menu_items (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(255),
  price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES categories(id),
  UNIQUE (category_id, name)
);

CREATE TABLE ingredients (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL UNIQUE,
  unit VARCHAR(30) NOT NULL,
  min_threshold DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (min_threshold >= 0)
);

CREATE TABLE inventory (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  ingredient_id BIGINT UNSIGNED NOT NULL UNIQUE,
  quantity DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (quantity >= 0),
  last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_inventory_ing FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

CREATE TABLE suppliers (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL UNIQUE,
  contact VARCHAR(120),
  phone VARCHAR(20),
  email VARCHAR(120)
);

CREATE TABLE purchases (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  supplier_id BIGINT UNSIGNED NOT NULL,
  ingredient_id BIGINT UNSIGNED NOT NULL,
  quantity DECIMAL(10,2) NOT NULL CHECK (quantity > 0),
  cost DECIMAL(10,2) NOT NULL CHECK (cost >= 0),
  purchased_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_purchase_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
  CONSTRAINT fk_purchase_ing FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
  INDEX (supplier_id), INDEX (ingredient_id)
);

CREATE TABLE discounts (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL UNIQUE,
  type ENUM('PERCENT','FLAT') NOT NULL,
  value DECIMAL(10,2) NOT NULL CHECK (value >= 0),
  applies_to ENUM('GENERAL','STUDENT','STAFF','LOYAL') NOT NULL
);

CREATE TABLE taxes (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL UNIQUE,
  rate DECIMAL(5,2) NOT NULL CHECK (rate >= 0)
);

CREATE TABLE orders (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  customer_name VARCHAR(120),
  customer_type ENUM('GENERAL','STUDENT','STAFF','LOYAL') DEFAULT 'GENERAL',
  status ENUM('PENDING','PAID','CANCELLED') NOT NULL DEFAULT 'PENDING',
  discount_id BIGINT UNSIGNED,
  tax_id BIGINT UNSIGNED,
  subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
  tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
  discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
  total DECIMAL(10,2) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_discount FOREIGN KEY (discount_id) REFERENCES discounts(id),
  CONSTRAINT fk_orders_tax FOREIGN KEY (tax_id) REFERENCES taxes(id),
  INDEX (created_at)
);

CREATE TABLE order_items (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT UNSIGNED NOT NULL,
  menu_item_id BIGINT UNSIGNED NOT NULL,
  quantity INT NOT NULL CHECK (quantity > 0),
  unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
  line_total DECIMAL(10,2) NOT NULL CHECK (line_total >= 0),
  CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_oi_menu FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
  INDEX (order_id), INDEX (menu_item_id)
);

CREATE TABLE payments (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT UNSIGNED NOT NULL,
  amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
  method ENUM('CASH','CARD','MFS') NOT NULL,
  paid_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reference VARCHAR(120),
  CONSTRAINT fk_pay_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  INDEX (order_id)
);

CREATE TABLE invoices (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT UNSIGNED NOT NULL UNIQUE,
  invoice_number VARCHAR(50) NOT NULL UNIQUE,
  payment_id BIGINT UNSIGNED,
  total DECIMAL(10,2) NOT NULL,
  issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_inv_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_inv_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);

CREATE TABLE audit_logs (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED,
  action VARCHAR(120) NOT NULL,
  entity VARCHAR(80),
  entity_id BIGINT,
  details TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Functions
DELIMITER //
CREATE FUNCTION calculate_tax(p_amount DECIMAL(10,2), p_tax_id BIGINT UNSIGNED)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
  DECLARE v_rate DECIMAL(5,2);
  SELECT rate INTO v_rate FROM taxes WHERE id = p_tax_id;
  RETURN IFNULL(p_amount * v_rate / 100, 0);
END//
CREATE FUNCTION calculate_discount(p_amount DECIMAL(10,2), p_discount_id BIGINT UNSIGNED, p_customer_type ENUM('GENERAL','STUDENT','STAFF','LOYAL'))
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
  DECLARE v_type ENUM('PERCENT','FLAT');
  DECLARE v_value DECIMAL(10,2);
  DECLARE v_apply ENUM('GENERAL','STUDENT','STAFF','LOYAL');
  SELECT type, value, applies_to INTO v_type, v_value, v_apply FROM discounts WHERE id = p_discount_id;
  IF v_type IS NULL OR (v_apply <> p_customer_type AND v_apply <> 'GENERAL') THEN
    RETURN 0;
  END IF;
  RETURN IF(v_type = 'PERCENT', p_amount * v_value / 100, LEAST(v_value, p_amount));
END//
CREATE FUNCTION get_total_order_price(p_order_id BIGINT UNSIGNED)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
  DECLARE v_total DECIMAL(10,2);
  SELECT SUM(line_total) INTO v_total FROM order_items WHERE order_id = p_order_id;
  RETURN IFNULL(v_total, 0);
END//
DELIMITER ;

-- Procedures
DELIMITER //
CREATE PROCEDURE create_order_procedure(
  IN p_customer_name VARCHAR(120),
  IN p_customer_type ENUM('GENERAL','STUDENT','STAFF','LOYAL'),
  IN p_tax_id BIGINT UNSIGNED,
  IN p_discount_id BIGINT UNSIGNED
)
BEGIN
  DECLARE v_order_id BIGINT UNSIGNED;
  DECLARE v_subtotal DECIMAL(10,2);
  DECLARE v_discount DECIMAL(10,2);
  DECLARE v_tax DECIMAL(10,2);
  DECLARE v_total DECIMAL(10,2);

  SET v_order_id = LAST_INSERT_ID();
  SET v_subtotal = get_total_order_price(v_order_id);
  SET v_discount = calculate_discount(v_subtotal, p_discount_id, p_customer_type);
  SET v_tax = calculate_tax(v_subtotal - v_discount, p_tax_id);
  SET v_total = v_subtotal - v_discount + v_tax;

  UPDATE orders
    SET subtotal = v_subtotal,
        discount_amount = v_discount,
        tax_amount = v_tax,
        total = v_total
  WHERE id = v_order_id;
END//

CREATE PROCEDURE generate_invoice_procedure(IN p_order_id BIGINT UNSIGNED, IN p_payment_id BIGINT UNSIGNED)
BEGIN
  DECLARE v_invoice_no VARCHAR(50);
  DECLARE v_total DECIMAL(10,2);
  SELECT total INTO v_total FROM orders WHERE id = p_order_id;
  SET v_invoice_no = CONCAT('INV-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(p_order_id, 6, '0'));
  INSERT INTO invoices (order_id, invoice_number, payment_id, total) VALUES (p_order_id, v_invoice_no, p_payment_id, v_total);
END//

CREATE PROCEDURE daily_sales_report_procedure(IN p_date DATE)
BEGIN
  SELECT 
    COUNT(DISTINCT o.id) AS orders_count,
    SUM(oi.quantity) AS items_sold,
    SUM(o.total) AS revenue
  FROM orders o
  JOIN order_items oi ON oi.order_id = o.id
  WHERE DATE(o.created_at) = p_date AND o.status <> 'CANCELLED';
END//
DELIMITER ;

-- Triggers
DELIMITER //
CREATE TRIGGER trg_users_before_insert
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
  IF CHAR_LENGTH(NEW.password_hash) < 20 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Password hash invalid';
  END IF;
  INSERT INTO audit_logs (user_id, action, entity, entity_id, details)
  VALUES (NEW.id, 'CREATE_USER', 'users', NULL, CONCAT('Created user ', NEW.username));
END//

CREATE TRIGGER trg_order_items_after_insert
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
  UPDATE inventory inv
  JOIN menu_items mi ON mi.id = NEW.menu_item_id
  JOIN ingredients ing ON ing.id = inv.ingredient_id
  SET inv.quantity = inv.quantity - NEW.quantity
  WHERE mi.id = NEW.menu_item_id;
END//

CREATE TRIGGER trg_payments_after_insert
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
  UPDATE orders SET status = 'PAID' WHERE id = NEW.order_id;
  INSERT INTO audit_logs (user_id, action, entity, entity_id, details)
  VALUES (NULL, 'PAYMENT', 'orders', NEW.order_id, CONCAT('Payment ', NEW.id, ' recorded'));
END//
DELIMITER ;

-- Seed data
INSERT INTO roles (name, description) VALUES ('ADMIN','Full access'), ('STAFF','POS and inventory');
INSERT INTO taxes (name, rate) VALUES ('VAT', 15.00);
INSERT INTO discounts (name, type, value, applies_to) VALUES ('Student 10', 'PERCENT', 10.00, 'STUDENT');
INSERT INTO categories (name) VALUES ('Coffee'), ('Bakery');
INSERT INTO menu_items (category_id, name, price, is_active) VALUES (1, 'Espresso', 3.50, 1), (1, 'Latte', 4.50, 1), (2, 'Croissant', 2.80, 1);
INSERT INTO ingredients (name, unit, min_threshold) VALUES ('Coffee Beans','g',500), ('Milk','ml',1000), ('Butter','g',500);
INSERT INTO inventory (ingredient_id, quantity) VALUES (1, 5000), (2, 5000), (3, 2000);
