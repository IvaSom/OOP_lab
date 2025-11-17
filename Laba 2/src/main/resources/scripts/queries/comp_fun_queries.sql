SELECT * FROM compFun WHERE id = ?;
SELECT * FROM compFun WHERE name = ?;

INSERT INTO compFun (name) VALUES (?);

UPDATE compFun SET name = ? WHERE id = ?;

DELETE FROM compFun WHERE id = ?;



--информация о стуктуре
SELECT cs.*, af.name as analytic_function_name, cf.name as composite_function_name
--делаем сокрвщения чтобы много не писать, их надо писать в одной конструкции внутри from или join
FROM composite_structure cs
--присоединяем таблицы по одинаковым id
JOIN analFun af ON cs.analytic_id = af.id
JOIN compFun cf ON cs.composite_id = cf.id
WHERE cs.composite_id = ?

--композитные функции, с конкретной аналитической функцией
SELECT cf.*
FROM compFun cf
JOIN composite_structure cs ON cf.id = cs.composite_id
WHERE cs.analytic_id = ?;

-- просто по id
SELECT * FROM composite_structure WHERE id = ?;

--найти по номеру выполнения и id композитной
SELECT * FROM composite_structure
WHERE composite_id = ? AND execution_order = ?;


-- добавить шаг
INSERT INTO composite_structure (composite_id, analytic_id, execution_order)
VALUES (?, ?, ?);


-- изменить порядок выполнения шага
UPDATE composite_structure
SET execution_order = ?
WHERE id = ?;

-- изменить аналитическую функцию в шаге
UPDATE composite_structure
SET analytic_id = ?
WHERE id = ?;


-- удалить шаг по id
DELETE FROM composite_structure WHERE id = ?;

--удалить шаг по порядковому номеру
DELETE FROM composite_structure
WHERE composite_id = ? AND execution_order = ?;
