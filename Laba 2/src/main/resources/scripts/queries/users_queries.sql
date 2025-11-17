SELECT * FROM users WHERE id = ?;
SELECT * FROM users WHERE login = ?;
SELECT * FROM users WHERE email = ?;
SELECT * FROM users WHERE name = ?;

-- есть ли пользователь по логину или почет
SELECT COUNT(*) as user_exists FROM users WHERE login = ?;
SELECT COUNT(*) as email_exists FROM users WHERE email = ?;

-- все пользователи
SELECT * FROM users ORDER BY name;

-- пользователь с логином и паролем
SELECT * FROM users WHERE login = ? AND password = ?;

-- добавить пользователя
INSERT INTO users (name, login, email, password)
VALUES (?, ?, ?, ?);

--обновить пользователя
UPDATE users
SET name = ?, login = ?, email = ?, password = ?
WHERE id = ?;

UPDATE users SET password = ? WHERE id = ?;
UPDATE users SET email = ? WHERE id = ?;
UPDATE users SET name = ? WHERE id = ?;

--удалить пользователя
DELETE FROM users WHERE id = ?;
DELETE FROM users WHERE login = ?;

--колво пользователей
SELECT COUNT(*) as total_users FROM users;