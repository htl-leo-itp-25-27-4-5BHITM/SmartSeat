INSERT INTO seatlocation (wing, floor)
VALUES ('Left',  '1OG'),
       ('Right', '1OG'),
       ('Left',  '2OG'),
       ('Right', '2OG');

INSERT INTO seat (name, unoccupied, location_id)
VALUES ('Koje 1', true, 1),
       ('Koje 2', true, 2),
       ('Koje 3', true, 2),
       ('Koje 4', true, 3),
       ('Koje 5', true, 4);

INSERT INTO endtimes (endtime)
VALUES (TIME '08:50'),
       (TIME '09:45'),
       (TIME '10:50'),
       (TIME '11:45'),
       (TIME '12:40'),
       (TIME '13:35'),
       (TIME '14:30'),
       (TIME '15:25'),
       (TIME '16:20'),
       (TIME '17:15');