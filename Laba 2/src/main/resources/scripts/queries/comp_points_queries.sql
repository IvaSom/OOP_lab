SELECT * FROM comp_points WHERE id = ?;

SELECT * FROM comp_points WHERE x = ?;

SELECT * FROM comp_points WHERE y = ?;

--все точки композитной функции
SELECT * FROM comp_points WHERE funID = ?;

--точки с информацией о композитной функции
SELECT cp.*, cf.name as composite_function_name
FROM comp_points cp
JOIN compFun cf ON cp.funID = cf.id
WHERE cp.funID = ?;


-- добавить точку
INSERT INTO comp_points (x, y, funID) VALUES (?, ?, ?);

--обновить точку
UPDATE comp_points
SET x = ?, y = ?, funID = ?
WHERE id = ?;

-- удалить точку
DELETE FROM comp_points WHERE id = ?;

-- удалить точки композитной функции
DELETE FROM comp_points WHERE funID = ?;