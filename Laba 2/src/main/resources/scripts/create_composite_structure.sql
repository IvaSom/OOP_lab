
CREATE TABLE composite_structure (
    id BIGSERIAL PRIMARY KEY,
    composite_id BIGINT NOT NULL,
    analytic_id BIGINT NOT NULL,
    --номер шага в цепочке выполнений
    execution_order INTEGER NOT NULL,

    FOREIGN KEY (composite_id) REFERENCES compFun(id) ON DELETE CASCADE,
    FOREIGN KEY (analytic_id) REFERENCES analFun(id),
    UNIQUE(composite_id, execution_order)
);