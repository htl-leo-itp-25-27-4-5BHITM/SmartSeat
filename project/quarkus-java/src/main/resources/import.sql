INSERT INTO seatlocation (wing, floor)
VALUES
    ('Left',  '1OG'),
    ('Right', '1OG'),
    ('Left',  '2OG'),
    ('Right', '2OG');


INSERT INTO seat (name, unoccupied, location_id, mapx, mapy)
VALUES
    ('Koje 1', true, 1, 0.31, 0.17),
    ('Koje 2', true, 2, 0.65, 0.17),
    ('Koje 3', true, 2, 0.68, 0.45),
    ('Koje 4', true, 3, 0.32, 0.45),
    ('Koje 5', true, 4, 0.68, 0.45);

INSERT INTO duration (seconds)
VALUES (35);

INSERT INTO users (username, password) VALUES ('admin',
'$2a$10$U2CGyz7osq40XL50dbdkL.MRUIca1RDnnq/k5U6PVFFLKikpmrrcq');
