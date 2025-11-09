SELECT * FROM users WHERE id = :id;
SELECT * FROM users WHERE login = :login;
SELECT * FROM users WHERE email = :email;
SELECT * FROM users WHERE name = :name;

-- есть ли пользователь по логину или почет
SELECT COUNT(*) as user_exists FROM users WHERE login = :login;
SELECT COUNT(*) as email_exists FROM users WHERE email = :email;

-- все пользователи
SELECT * FROM users ORDER BY name;

-- пользователь с логином и паролем
SELECT * FROM users WHERE login = :login AND password = :password;

-- добавить пользователя
INSERT INTO users (name, login, email, password)
VALUES (:name, :login, :email, :password);

--обновить пользователя
UPDATE users
SET name = :name, login = :login, email = :email, password = :password
WHERE id = :id;

UPDATE users SET password = :newPassword WHERE id = :id;
UPDATE users SET email = :newEmail WHERE id = :id;
UPDATE users SET name = :newName WHERE id = :id;

--удалить пользователя
DELETE FROM users WHERE id = :id;
DELETE FROM users WHERE login = :login;

--колво пользователей
SELECT COUNT(*) as total_users FROM users;
