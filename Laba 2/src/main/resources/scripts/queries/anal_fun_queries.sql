SELECT * FROM anal_fun WHERE id = :id;
SELECT * FROM anal_fun WHERE name = :name;
SELECT * FROM anal_fun WHERE type = :type;

INSERT INTO anal_fun (name, type) VALUES (:name, :type);

UPDATE anal_fun SET name = :name, type = :type WHERE id = :id;

DELETE FROM anal_fun WHERE id = :id;