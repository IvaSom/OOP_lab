SELECT * FROM tab_points WHERE id = :id;
SELECT * FROM tab_points WHERE x = :x;
SELECT * FROM tab_points WHERE y = :y;
SELECT * FROM tab_points WHERE derive = :derive;
SELECT * FROM tab_points WHERE funID = :funID;

--получить информацию о функции с точками
SELECT tp.*, tf.type as table_function_type
FROM tab_points tp
JOIN tabFun tf ON tp.funID = tf.id
WHERE tp.funID = :funID;

INSERT INTO tab_points (x, y, derive, funID)
VALUES (:x, :y, :derive, :funID);

UPDATE tab_points
SET x = :x, y = :y, derive = :derive, funID = :funID
WHERE id = :id;

DELETE FROM tab_points WHERE id = :id;
--удалить все точки функции
DELETE FROM tab_points WHERE funID = :funID;