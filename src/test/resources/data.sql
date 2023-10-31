INSERT INTO USERS
VALUES (1, 'name1', 'email1@email'),
       (2, 'name2', 'email2@email'),
       (3, 'name3', 'email3@email');

INSERT INTO ITEMS
VALUES (1, 'name1', 'description1', TRUE, null, 1),
       (2, 'name2', 'description2', FALSE, null, 1);
INSERT INTO BOOKINGS
VALUES (1, '2023-10-01', '2023-10-10', 1, 2, 'WAITING');