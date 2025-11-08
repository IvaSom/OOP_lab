CREATE TABLE functions (
    id BIGSERIAL PRIMARY KEY,
    -- у нас 10 аналитических функций
    funID INTEGER CHECK (funID >= 1 AND funID <= 10),
    type VARCHAR(10) CHECK (type IN ('anal', 'tab', 'comp'))
);