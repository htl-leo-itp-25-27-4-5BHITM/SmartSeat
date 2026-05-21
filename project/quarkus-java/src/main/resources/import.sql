INSERT INTO seatlocation (wing, floor)
VALUES
    ('Left',  '1OG'),
    ('Right', '1OG'),
    ('Left',  '2OG'),
    ('Right', '2OG');


INSERT INTO seat (name, unoccupied, location_id)
VALUES
    ('Koje 1', true, 1),
    ('Koje 2', true, 2),
    ('Koje 3', true, 2),
    ('Koje 4', true, 3),
    ('Koje 5', true, 4);

INSERT INTO duration (seconds)
VALUES (35);

INSERT INTO users (username, password) VALUES ('admin',
'$2a$10$U2CGyz7osq40XL50dbdkL.MRUIca1RDnnq/k5U6PVFFLKikpmrrcq');