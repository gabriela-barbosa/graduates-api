insert into advisor (id, created_at, user_id)
values (1, now(), 6),
       (2, now(), 8),
       (3, now(), 9),
       (4, now(), 10),
       (5, now(), 11),
       (6, now(), 12)
;
insert into platform_user (id, email, name, password, created_at)
values (1, 'gabriela@gmail.com', 'Gabriela', '', now()),
       (2, 'davi@gmail.com', 'Davi', '', now()),
       (3, 'victor@gmail.com', 'Victor', '', now()),
       (4, 'arthur@gmail.com', 'Arthur', '', now()),
       (5, 'luiza@gmail.com', 'Luiza', '', now()),
       (6, 'vanessa@gmail.com', 'Vanessa', '', now()),
       (7, 'gabriel@gmail.com', 'Gabriel', '', now()),
       (8, 'anselmo@gmail.com', 'Anselmo', '', now()),
       (9, 'leonardo@gmail.com', 'Leonardo', '', now()),
       (10, 'plastino@gmail.com', 'Plastino', '', now()),
       (11, 'troy@gmail.com', 'Troy', '', now()),
       (12, 'aline@gmail.com', 'Aline', '', now())
;
insert into graduate (id, created_at, history_status_id, user_id)
values (1, now(), 1, 1),
       (2, now(), 2, 2),
       (3, now(), 3, 3),
       (4, now(), null, 4),
       (5, now(), 4, 5),
       (6, now(), null, 7)
;
insert into institution (id, created_at, name, type)
values (1, now(), 'OLX', 1),
       (2, now(), 'BNDES', 2),
       (3, now(), 'PWC', 3),
       (4, now(), 'M4U', 1),
       (5, now(), 'SNC', 1),
       (6, now(), 'Alura', 1)
;
insert into work_history (id, created_at, "position", graduate_id, institution_id, updated_at)
values (1, now(), 'Dev Jr', 1, 1, null),
       (2, now(), 'Estagiário', 5, 2, null),
       (3, now(), 'Estagiário', 1, 2, null),
       (4, now(), 'Senior Associate', 2, 3, null),
       (5, now(), 'Dev Jr', 1, 4, null),
       (6, now(), 'Dev Jr', 2, 5, null)
;
insert into history_status (id, created_at, known_workplace)
values (1, now(), true),
       (2, now(), true),
       (3, now(), false),
       (4, now(), true)
;
insert into course (id, created_at, minute_defense, program, advisor_id, graduate_id)
values (1, now(), 1, 1, 1, 1),
       (2, now(), 2, 2, 2, 2),
       (3, now(), 3, 1, 3, 3),
       (4, now(), 4, 1, 4, 4),
       (5, now(), 5, 2, 5, 5),
       (6, now(), 6, 2, 6, 6)
;