SELECT * FROM compFun WHERE id = :id;
SELECT * FROM compFun WHERE name = :name;

INSERT INTO compFun (name) VALUES (:name);

UPDATE compFun SET name = :newName WHERE id = :id;

DELETE FROM compFun WHERE id = :id;



--информация о стуктуре
SELECT cs.*, af.name as analytic_function_name, cf.name as composite_function_name
--делаем сокрвщения чтобы много не писать, их надо писать в одной конструкции внутри from или join
FROM composite_structure cs
--присоединяем таблицы по одинаковым id
JOIN analFun af ON cs.analytic_id = af.id
JOIN compFun cf ON cs.composite_id = cf.id
WHERE cs.composite_id = :compositeId

--композитные функции, с конкретной аналитической функцией
SELECT cf.*
FROM compFun cf
JOIN composite_structure cs ON cf.id = cs.composite_id
WHERE cs.analytic_id = :analyticId;

-- просто по id
SELECT * FROM composite_structure WHERE id = :id;

--найти по номеру выполнения и id композитной
SELECT * FROM composite_structure
WHERE composite_id = :compositeId AND execution_order = :order;


-- добавить шаг
INSERT INTO composite_structure (composite_id, analytic_id, execution_order)
VALUES (:compositeId, :analyticId, :executionOrder);


-- изменить порядок выполнения шага
UPDATE composite_structure
SET execution_order = :newOrder
WHERE id = :id;

-- изменить аналитическую функцию в шаге
UPDATE composite_structure
SET analytic_id = :newAnalyticId
WHERE id = :id;


-- удалить шаг по id
DELETE FROM composite_structure WHERE id = :id;

--удалить шаг по порядковому номеру
DELETE FROM composite_structure
WHERE composite_id = :compositeId AND execution_order = :order;
