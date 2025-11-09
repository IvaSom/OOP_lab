SELECT * FROM comp_points WHERE id = :id;

SELECT * FROM comp_points WHERE x = :x;

SELECT * FROM comp_points WHERE y = :y;

SELECT * FROM comp_points WHERE derive = :derive;
--все точки композитной функции
SELECT * FROM comp_points WHERE funID = :funID;

--точки с информацией о композитной функции
SELECT cp.*, cf.name as composite_function_name
FROM comp_points cp
JOIN compFun cf ON cp.funID = cf.id
WHERE cp.funID = :funID;


-- добавить точку
INSERT INTO comp_points (x, y, derive, funID) VALUES (:x, :y, :derive, :funID);

--обновить точку
UPDATE comp_points
SET x = :x, y = :y, derive = :derive, funID = :funID
WHERE id = :id;

-- удалить точку
DELETE FROM comp_points WHERE id = :id;

-- удалить точки композитной функции
DELETE FROM comp_points WHERE funID = :funID;
