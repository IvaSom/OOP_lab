SELECT * FROM tab_points WHERE id = ?;
SELECT * FROM tab_points WHERE x = ?;
SELECT * FROM tab_points WHERE y = ?;
SELECT * FROM tab_points WHERE derive = ?;
SELECT * FROM tab_points WHERE funID = ?;

--получить информацию о функции с точками
SELECT tp.*, tf.type as table_function_type
FROM tab_points tp
JOIN tabFun tf ON tp.funID = tf.id
WHERE tp.funID = ?;

INSERT INTO tab_points (x, y, derive, funID)
VALUES (?, ?, ?, ?);

UPDATE tab_points
SET x = ?, y = ?, derive = ?, funID = ?
WHERE id = ?;

DELETE FROM tab_points WHERE id = ?;
--удалить все точки функции
DELETE FROM tab_points WHERE funID = ?;