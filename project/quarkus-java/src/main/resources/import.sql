INSERT INTO SEATLOCATION (WING, FLOOR)
VALUES ('Left', '1OG'),
       ('Right', '1OG'),
       ('Left', '2OG'),
       ('Right', '2OG');

INSERT INTO SEAT(Name, unoccupied, LOCATION_ID)
VALUES ('Koje 1', true, 1),
       ('Koje 2', true, 2),
       ('Koje 3', true, 2),
       ('Koje 4', false, 3),
       ('Koje 5', false, 4);

INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('8:50', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('9:45', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('10:50', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('11:45', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('12:40', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('13:35', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('14:30', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('15:25', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('16:20', 'HH24:mi'));
INSERT INTO ENDTIMES (ENDTIME) VALUES (to_char('17:15', 'HH24:mi'));