USE cafedb;

-- Insert remaining payments (triggers are dropped so no conflict)
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

-- Update PAID orders status
UPDATE orders SET status = 'PAID' WHERE customer_name IN ('Alice Johnson','Bob Smith','Charlie Davis','Diana Prince','Edward Norton','Fiona Green','George Harris','Hannah Lee','Julia White','Karl Fischer','Lara Croft','Mike Tyson','Oscar Wilde','Priya Sharma','Quinn Hughes','Rachel Adams');

-- Insert invoices for all paid orders that don't have one yet
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

-- Insert attendance records
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

-- Re-create dropped triggers
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

SELECT 'Remaining data inserted and triggers restored!' AS result;
