USE cafedb;
DELIMITER //
CREATE TRIGGER trg_payments_after_insert
AFTER INSERT ON payments FOR EACH ROW
BEGIN
  UPDATE orders SET status = 'PAID' WHERE id = NEW.order_id;
  INSERT INTO audit_logs (user_id, action, entity, entity_id, details)
  VALUES (NULL, 'PAYMENT', 'orders', NEW.order_id, CONCAT('Payment ', NEW.id, ' recorded'));
END //
DELIMITER ;
SELECT 'Trigger restored!' AS result;
