CREATE TABLE points (
    id BIGSERIAL PRIMARY KEY,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    derive DOUBLE PRECISION NOT NULL,
    --это типа мы ссылаемся на наши аналитические функции
    funID INTEGER CHECK (funID >= 1 AND funID <= 10)
);