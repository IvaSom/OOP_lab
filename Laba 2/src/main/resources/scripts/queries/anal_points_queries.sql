SELECT * FROM anal_points WHERE id = ?;
SELECT * FROM anal_points WHERE x = ?;
SELECT * FROM anal_points WHERE y = ?;
SELECT * FROM anal_points WHERE funID = ?;

INSERT INTO anal_points (x, y, funID) VALUES (?, ?, ?);

UPDATE anal_points SET x = ?, y = ?, funID = ? WHERE id = ?;

DELETE FROM anal_points WHERE id = ?;