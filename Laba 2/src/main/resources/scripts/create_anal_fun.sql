CREATE TABLE analFun (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    --как я понял мы просто присвоим каждой нашей функции тип в виде числа
    type INTEGER CHECK (type >= 1 AND type <= 10)
);
