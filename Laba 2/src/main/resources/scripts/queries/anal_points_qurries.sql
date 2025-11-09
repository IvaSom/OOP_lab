SELECT * FROM anal_points WHERE id = :id;
SELECT * FROM anal_points WHERE x = :x;
SELECT * FROM anal_points WHERE y = :y;
SELECT * FROM anal_points WHERE derive = :derive;
SELECT * FROM anal_points WHERE funID = :funID;

INSERT INTO anal_points (x, y, derive, funID) VALUES (:x, :y, :derive, :funID);

UPDATE anal_points SET x = :x, y = :y, derive = :derive, funID = :funID WHERE id = :id;

DELETE FROM anal_points WHERE id = :id;