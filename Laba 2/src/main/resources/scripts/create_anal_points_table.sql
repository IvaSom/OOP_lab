CREATE TABLE anal_points (
    id BIGSERIAL PRIMARY KEY,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    derive DOUBLE PRECISION NOT NULL,
    funID INTEGER NOT NULL,

    --это типа мы ссылаемся на наши аналитические функции
    CONSTRAINT funID_fk FOREIGN KEY (funID) REFERENCES analFun (id)
);