SELECT * FROM tabFun WHERE id = :id;
SELECT * FROM tabFun WHERE type = :type;

INSERT INTO tabFun (type) VALUES (:type);

UPDATE tabFun SET type = :type WHERE id = :id;

DELETE FROM tabFun WHERE id = :id;