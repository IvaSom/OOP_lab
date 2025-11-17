SELECT * FROM composite_structure WHERE id = ?;

-- Для метода findAll()
SELECT * FROM composite_structure ORDER BY execution_order;

-- Для метода create(CompositeStructure compositeStructure)
INSERT INTO composite_structure (composite_id, analytic_id, execution_order) VALUES (?, ?, ?);

-- Для метода delete(Long id)
DELETE FROM composite_structure WHERE id = ?;