INSERT INTO contact VALUES
(1001, '123456789', 0, 'Mario', 'Rossi'),
(1002, '123456788', 0, 'Giacomo', 'Verdi'),
(5003, '123456787', 1, 'Professional', 'Test'),
(1003, '123456787', 1, 'Mario', 'Giallo'),
(1203, '123456787', 1, 'Andrea', 'Verde'),
(1204, '123456787', 1, 'Andrea', 'Bianchino'),
(1304, '123456787', 1, 'Giuseppe', 'Viola'),
(1404, '123456787', 1, 'Giuseppe', 'Sasso'),
(1004, '123456786', 2, 'Luca', 'Nero');

INSERT INTO email VALUES
(1001, 'mariorossi@gmail.com'),
(1002,'test@mail.com');

INSERT INTO telephone VALUES
(1001,3568899777),
(1002,3243253525);

INSERT INTO address VALUES
(1002,'Turin','Italy','Street Test','11'),
(1001,'Turin','Italy','Street Test','12');

INSERT INTO contact_email VALUES
(1001,1001),
(5003,1002);

INSERT INTO contact_address VALUES
(5003,1002);

INSERT INTO contact_telephone VALUES
(5003,1001);

INSERT INTO customer VALUES
(1001,1001),
(1002,1002);

INSERT INTO professional VALUES
(1001,2.4,1,'Italy','backend',1003),
(1203,3.2,1,'Italy','frontend',1203),
(1204,3.0,1,'Italy','frontend',1204),
(1304,3.0,1,'Italy','frontend',1304),
(2404,4.2,0,'Italy','frontend',5003),
(1404,4.2,0,'Italy','frontend',1404);


INSERT INTO job_offer VALUES
(1111,'that is a good job',12,2.9,'frontend',0,1001,null),
(1211,'aborted job offer',12,2.9,'frontend',5,1001,1001),
(1122,'good job offer',13,2,'backend',3,1001,1404),
(1222,'good job offer',13,2.8,'backend',2,1001,null),
(1123,'that is a good job',11,2.9,'backend',4,1001,1404);

INSERT INTO action_on_job VALUES
(1001,'2024-05-16 10:00:00','test action',0,1111,null),
(1002,'2024-05-16 10:00:00','test action',3,1122,1404);

INSERT INTO note VALUES
(1001,'2024-05-16','example note',1001,null);

INSERT INTO message VALUES
(1022,'test',0,'2024-05-16',0,0,'test',null,null,1002),
(1001,'test',3,'2024-05-16',0,0,'test',1001,null,null);

INSERT INTO action_on_message VALUES
(1001,'test','2024-05-16 10:00:00',0,1001);