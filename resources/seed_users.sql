SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO users (username, email, password_hash, full_name, phone, is_active) VALUES
('admin',  'admin@databrew.com',  'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', 'Admin User',   '0000000000', 1),
('admin1', 'admin1@databrew.com', '9c9a5ec9f469b3b49cdf2d8ac6d4bd351f8c07ce4cc8b6d9fa2fd1718df1924a', 'Admin One',    '0000000001', 1),
('admin2', 'admin2@databrew.com', '3c77bc053f7a6c628d7439bba01c3eb75fa0d89e416097c1a292f625dde37c6b', 'Admin Two',    '0000000002', 1),
('admin3', 'admin3@databrew.com', '60d6c689b7f1aade2fc63e0cd6c67059901e1750cdc9953cec2602b78631a6c4', 'Admin Three',  '0000000003', 1),
('admin4', 'admin4@databrew.com', 'a5337caa296f34a6484cb7eb54ba182af66145a56e92928e5b02d2b3c4fb7fa6', 'Admin Four',   '0000000004', 1),
('admin5', 'admin5@databrew.com', '3e9996d4a0aeffddf56cf3b3bb20d383d6ed6e5d906549c6a6e7bf866c48e71f', 'Admin Five',   '0000000005', 1);

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1);

SET FOREIGN_KEY_CHECKS = 1;
