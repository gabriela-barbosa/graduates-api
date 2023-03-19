insert into platform_user (id, email, name, password, created_at)
values (gen_random_uuid(), 'gabriela@gmail.com', 'Gabriela',
        '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now()),
       (gen_random_uuid(), 'davi@gmail.com', 'Davi', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'victor@gmail.com', 'Victor', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'arthur@gmail.com', 'Arthur', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'luiza@gmail.com', 'Luiza', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'vanessa@gmail.com', 'Vanessa',
        '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now()),
       (gen_random_uuid(), 'gabriel@gmail.com', 'Gabriel',
        '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now()),
       (gen_random_uuid(), 'anselmo@gmail.com', 'Anselmo',
        '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now()),
       (gen_random_uuid(), 'leonardo@gmail.com', 'Leonardo',
        '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now()),
       (gen_random_uuid(), 'plastino@gmail.com', 'Plastino',
        '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now()),
       (gen_random_uuid(), 'troy@gmail.com', 'Troy', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'aline@gmail.com', 'Aline', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now())
;


insert into platform_user (id, email, name, password, created_at)
values (gen_random_uuid(), 'gab@gmail.com', 'Gabrielly', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'davidson@gmail.com', 'Davidson',
        '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now()),
       (gen_random_uuid(), 'viktor@gmail.com', 'Viktor', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'artu@gmail.com', 'Artu', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'louise@gmail.com', 'Louise', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now()),
       (gen_random_uuid(), 'valeria@gmail.com', 'Valéria',
        '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u', now()),
       (gen_random_uuid(), 'alice@gmail.com', 'Alice', '$2a$10$j4fJVllmbSalkl1.M0bDE.Fm2sqjRj8r50pykWQVd35TIqFwlPX0u',
        now())
;

insert into platform_user_role(platform_user_id, role)
VALUES ('fe52dbee-c4e2-46b3-b48a-2292a1a3e84b', 'GRADUATE'),
       ('f9194fdf-43b4-4730-ae79-f529f94c7036', 'GRADUATE'),
       ('f8070687-cf08-4b59-bcca-016e992bd5f4', 'GRADUATE'),
       ('f6336a47-33d3-473c-921c-7818f892158d', 'GRADUATE'),
       ('ed42630c-ff94-47da-a727-5a0316b9d320', 'GRADUATE'),
       ('e629f2d2-49b0-49d8-93b9-ead61095c26c', 'GRADUATE'),
       ('b706727c-786c-4daa-9e84-946451946dbc', 'PROFESSOR'),

       ('aed6b967-e947-41eb-b9c5-52606176a545', 'GRADUATE'),
       ('90a00332-666b-4072-95f0-5c16b0e34f5d', 'GRADUATE'),
       ('88190fca-7db3-43ad-ac17-c7efa6431236', 'ADMIN'),
       ('84bcb5f0-5d23-45a9-91a0-545d7c849bfb', 'GRADUATE'),
       ('8005e55a-8d0d-45c1-8f9b-dc4975d0bfc8', 'PROFESSOR'),

       ('4f08fb64-603c-499d-bf16-f850d4a7095c', 'PROFESSOR'),

       ('3cd93430-855e-43ab-a7a0-f5af573b4c2d', 'GRADUATE'),
       ('3aac5c83-7a36-4ef4-93e3-e0e211b56ffa', 'GRADUATE'),
       ('2785a7e0-1789-4d6d-bdae-dd367ef6b397', 'GRADUATE'),
       ('23757452-2ea8-4b2d-8caa-72f7f7bc6936', 'PROFESSOR'),

       ('2128f4f3-009b-45ee-af99-cd921f5e1d72', 'PROFESSOR'),

       ('06c2a675-71d3-4594-95e2-9bab0817f35e', 'GRADUATE'),
       ('f8070687-cf08-4b59-bcca-016e992bd5f4', 'PROFESSOR')
;



insert into advisor (id, created_at, user_id)
values (gen_random_uuid(), now(), 'b706727c-786c-4daa-9e84-946451946dbc'),
       (gen_random_uuid(), now(), '8005e55a-8d0d-45c1-8f9b-dc4975d0bfc8'),
       (gen_random_uuid(), now(), '4f08fb64-603c-499d-bf16-f850d4a7095c'),
       (gen_random_uuid(), now(), '23757452-2ea8-4b2d-8caa-72f7f7bc6936'),
       (gen_random_uuid(), now(), '2128f4f3-009b-45ee-af99-cd921f5e1d72'),
       (gen_random_uuid(), now(), 'f8070687-cf08-4b59-bcca-016e992bd5f4')
;

insert into history_status (id, created_at, known_workplace, status)
values (gen_random_uuid(), now(), true, 'PENDING'),
       (gen_random_uuid(), now(), true, 'PENDING'),
       (gen_random_uuid(), now(), false, 'PENDING'),
       (gen_random_uuid(), now(), true, 'PENDING')
;

insert into graduate (id, created_at, history_status_id, user_id)
values (gen_random_uuid(), now(), '39393f16-f0ab-4ab1-8e51-89704470e8e0', 'fe52dbee-c4e2-46b3-b48a-2292a1a3e84b'),
       (gen_random_uuid(), now(), 'bfccb1cc-7d0c-4766-b595-e7e90d8deae6', 'f9194fdf-43b4-4730-ae79-f529f94c7036'),
       (gen_random_uuid(), now(), '601b7636-f8e5-454a-89a3-1f63a6474298', 'f8070687-cf08-4b59-bcca-016e992bd5f4'),
       (gen_random_uuid(), now(), null, 'f6336a47-33d3-473c-921c-7818f892158d'),
       (gen_random_uuid(), now(), 'e7e6b627-8b37-45ba-a71f-619dffa11816', 'ed42630c-ff94-47da-a727-5a0316b9d320'),
       (gen_random_uuid(), now(), null, 'e629f2d2-49b0-49d8-93b9-ead61095c26c'),
       (gen_random_uuid(), now(), null, '4f08fb64-603c-499d-bf16-f850d4a7095c'),
       (gen_random_uuid(), now(), null, '3cd93430-855e-43ab-a7a0-f5af573b4c2d'),
       (gen_random_uuid(), now(), null, '3aac5c83-7a36-4ef4-93e3-e0e211b56ffa'),
       (gen_random_uuid(), now(), null, '2785a7e0-1789-4d6d-bdae-dd367ef6b397'),
       (gen_random_uuid(), now(), null, '23757452-2ea8-4b2d-8caa-72f7f7bc6936'),
       (gen_random_uuid(), now(), null, '2128f4f3-009b-45ee-af99-cd921f5e1d72')
;

insert into cnpqlevel (id, level, created_at, active)
values (gen_random_uuid(), 'PQ2', now(), true),
       (gen_random_uuid(), 'PQ1D', now(), true),
       (gen_random_uuid(), 'PQ1C', now(), true),
       (gen_random_uuid(), 'PQ1B', now(), true),
       (gen_random_uuid(), 'PQ1A', now(), true);

insert into ci_program (id, initials, created_at, active)
values (gen_random_uuid(), 'PGC', now(), true),
       (gen_random_uuid(), 'CAA', now(), true)
;

insert into institution_type (id, name, created_at)
values (gen_random_uuid(), 'Universidade pública federal', now()),
       (gen_random_uuid(), 'Instituição pública federal', now()),
       (gen_random_uuid(), 'Instituição pública estadual', now()),
       (gen_random_uuid(), 'Instituição privada', now()),
       (gen_random_uuid(), 'Organização pública', now()),
       (gen_random_uuid(), 'Empresa ou instituição brasileira', now()),
       (gen_random_uuid(), 'Empresa ou instituição estrangeira', now()),
       (gen_random_uuid(), 'Outros', now())
;


insert into institution (id, created_at, name, type_id)
values (gen_random_uuid(), now(), 'OLX', '997e504e-bc71-40cf-99da-f4c64cd31448'),
       (gen_random_uuid(), now(), 'BNDES', '85448e00-8097-426f-af9d-6eb434cce877'),
       (gen_random_uuid(), now(), 'PWC', '6858aa91-81ef-41ab-928e-30d1f5a75420'),
       (gen_random_uuid(), now(), 'M4U', '997e504e-bc71-40cf-99da-f4c64cd31448'),
       (gen_random_uuid(), now(), 'SNC', '997e504e-bc71-40cf-99da-f4c64cd31448'),
       (gen_random_uuid(), now(), 'Alura', '997e504e-bc71-40cf-99da-f4c64cd31448')
;

insert into work_history (id, created_at, "position", graduate_id, institution_id, updated_at, status)
values (gen_random_uuid(), now(), 'Dev Jr', '07aa785c-301c-43e4-8286-989950a880f9', '9e13d8c8-d48f-4d34-b1b1-f18bea72598e', null, 1),
       (gen_random_uuid(), now(), 'Estagiário', 'd6e22b02-da74-4a52-b69c-b8a178754412', 'b0a7b1f1-9544-484b-88f6-9bfc948a9427', null, 1),
       (gen_random_uuid(), now(), 'Estagiário', '07aa785c-301c-43e4-8286-989950a880f9', 'b0a7b1f1-9544-484b-88f6-9bfc948a9427', null, 1),
       (gen_random_uuid(), now(), 'Senior Associate', '94b55af5-82dd-440f-aa57-d60916442bf1', 'b5f3d4a0-c2f6-4994-874f-825afdf1db50', null, 1),
       (gen_random_uuid(), now(), 'Dev Jr', '07aa785c-301c-43e4-8286-989950a880f9', 'd9dfce9e-0b89-4074-8fc6-1294c19ea4e8', null, 1),
       (gen_random_uuid(), now(), 'Dev Jr', '94b55af5-82dd-440f-aa57-d60916442bf1', '22bcad2a-333d-4a20-bb6f-7d199e1b05f5', null, 1)
;

insert into course (id, created_at, minute_defense, program_id, advisor_id, graduate_id)
values (gen_random_uuid(), now(), 1, 'f1643b6d-0036-4982-9635-39a7029bfc3b', '3e853481-d5da-4984-ab5c-30d3aea21c39', '07aa785c-301c-43e4-8286-989950a880f9'),
       (gen_random_uuid(), now(), 2, '9ddf6187-9f6e-4ba5-b425-3baed3ba1d4e', 'a4cb905d-96c3-44bc-afd8-80ea87ee9a62', '94b55af5-82dd-440f-aa57-d60916442bf1'),
       (gen_random_uuid(), now(), 3, 'f1643b6d-0036-4982-9635-39a7029bfc3b', 'f5f56998-6ca6-421e-a3e4-ac401d661d0f', '3020232d-cae9-4dc8-b443-cf2ff45e4d21'),
       (gen_random_uuid(), now(), 4, 'f1643b6d-0036-4982-9635-39a7029bfc3b', 'da3ede74-eb2a-40b6-b966-8da2c75dbaa2', '91cccaa0-d488-4669-8aec-61a0fccd3b2c'),
       (gen_random_uuid(), now(), 5, '9ddf6187-9f6e-4ba5-b425-3baed3ba1d4e', '1d943fb3-79af-4d93-8cd0-acccf56d90c3', 'd6e22b02-da74-4a52-b69c-b8a178754412'),
       (gen_random_uuid(), now(), 6, '9ddf6187-9f6e-4ba5-b425-3baed3ba1d4e', '06a9b3ef-969f-4294-a1c1-1c610b708a70', '38e6aaeb-4d3c-4471-a5fa-f9686eeef110')
;

insert into email (id, title, name, content, button_text, button_url, is_graduate_email, active, created_at)
values (gen_random_uuid(), 'Primeiro Email', 'Primeiro nome', 'Esse ;e o conteúdo do primeiro email', 'Clique Aqui', 'www.google.com',
        true, true, now()),
       (gen_random_uuid(), 'Segundo Email', 'Segundo nome', 'Esse ;e o conteúdo do segundo email', 'Clique Aqui', 'www.google.com',
        false, true, now())
;