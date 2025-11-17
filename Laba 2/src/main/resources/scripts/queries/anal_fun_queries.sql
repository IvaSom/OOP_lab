SELECT * FROM analFun WHERE id = ?;
SELECT * FROM analFun WHERE name = ?;
SELECT * FROM analFun WHERE type = ?;

INSERT INTO analFun (name, type) VALUES (?, ?);

UPDATE analFun SET name = ?, type = ? WHERE id = ?;

DELETE FROM analFun WHERE id = ?;