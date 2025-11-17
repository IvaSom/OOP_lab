SELECT * FROM tabFun WHERE id = ?;
SELECT * FROM tabFun WHERE type = ?;

INSERT INTO tabFun (type) VALUES (?);

UPDATE tabFun SET type = ? WHERE id = ?;

DELETE FROM tabFun WHERE id = ?;