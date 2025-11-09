SELECT * FROM analFun WHERE id = :id;
SELECT * FROM analFun WHERE name = :name;
SELECT * FROM analFun WHERE type = :type;

INSERT INTO analFun (name, type) VALUES (:name, :type);

UPDATE analFun SET name = :name, type = :type WHERE id = :id;

DELETE FROM analFun WHERE id = :id;