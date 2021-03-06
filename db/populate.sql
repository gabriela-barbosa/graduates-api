insert into platform_user (id, email, name, password, created_at, role)
values (1, 'gabriela@gmail.com', 'Gabriela', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (2, 'davi@gmail.com', 'Davi', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (3, 'victor@gmail.com', 'Victor', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (4, 'arthur@gmail.com', 'Arthur', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (5, 'luiza@gmail.com', 'Luiza', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (6, 'vanessa@gmail.com', 'Vanessa', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 2),
       (7, 'gabriel@gmail.com', 'Gabriel', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (8, 'anselmo@gmail.com', 'Anselmo', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 2),
       (9, 'leonardo@gmail.com', 'Leonardo', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 2),
       (10, 'plastino@gmail.com', 'Plastino', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 2),
       (11, 'troy@gmail.com', 'Troy', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 2),
       (12, 'aline@gmail.com', 'Aline', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 2)
;


insert into platform_user (id, email, name, password, created_at, role)
values (13, 'gab@gmail.com', 'Gabrielly', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (14, 'davidson@gmail.com', 'Davidson', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (15, 'viktor@gmail.com', 'Viktor', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (16, 'artu@gmail.com', 'Artu', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (17, 'louise@gmail.com', 'Louise', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (18, 'valeria@gmail.com', 'Val??ria', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 1),
       (19, 'alice@gmail.com', 'Alice', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now(), 0)
;

insert into advisor (id, created_at, user_id)
values (1, now(), 6),
       (2, now(), 8),
       (3, now(), 9),
       (4, now(), 10),
       (5, now(), 11),
       (6, now(), 12)
;

insert into history_status (id, created_at, known_workplace, status)
values (1, now(), true, 0),
       (2, now(), true, 0),
       (3, now(), false, 0),
       (4, now(), true, 0)
;

insert into graduate (id, created_at, history_status_id, user_id)
values (1, now(), 1, 1),
       (2, now(), 2, 2),
       (3, now(), 3, 3),
       (4, now(), null, 4),
       (5, now(), 4, 5),
       (6, now(), null, 7)
;

insert into cnpqlevel (id, level, created_at, active)
values (1, 'PQ2', now(), true),
       (2, 'PQ1D', now(), true),
       (3, 'PQ1C', now(), true),
       (4, 'PQ1B', now(), true),
       (5, 'PQ1A', now(), true);

insert into ci_program (id, initials, created_at, active)
values (1, 'PGC', now(), true),
       (2, 'CAA', now(), true)
;

insert into institution_type (id, name, created_at)
values (1, 'Universidade p??blica federal', now()),
       (2, 'Institui????o p??blica federal', now()),
       (3, 'Institui????o p??blica estadual', now()),
       (4, 'Institui????o privada', now()),
       (5, 'Organiza????o p??blica', now()),
       (6, 'Empresa ou institui????o brasileira', now()),
       (7, 'Empresa ou institui????o estrangeira', now()),
       (8, 'Outros', now())
;


insert into institution (id, created_at, name, type_id)
values (1, now(), 'OLX', 1),
       (2, now(), 'BNDES', 2),
       (3, now(), 'PWC', 3),
       (4, now(), 'M4U', 1),
       (5, now(), 'SNC', 1),
       (6, now(), 'Alura', 1)
;

insert into work_history (id, created_at, "position", graduate_id, institution_id, updated_at)
values (1, now(), 'Dev Jr', 1, 1, null),
       (2, now(), 'Estagi??rio', 5, 2, null),
       (3, now(), 'Estagi??rio', 1, 2, null),
       (4, now(), 'Senior Associate', 2, 3, null),
       (5, now(), 'Dev Jr', 1, 4, null),
       (6, now(), 'Dev Jr', 2, 5, null)
;

insert into course (id, created_at, minute_defense, program_id, advisor_id, graduate_id)
values (1, now(), 1, 1, 1, 1),
       (2, now(), 2, 2, 2, 2),
       (3, now(), 3, 1, 3, 3),
       (4, now(), 4, 1, 4, 4),
       (5, now(), 5, 2, 5, 5),
       (6, now(), 6, 2, 6, 6)
;