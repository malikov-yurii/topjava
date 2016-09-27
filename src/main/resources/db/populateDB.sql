DELETE FROM meals;
DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password');

INSERT INTO users (name, email, password)
VALUES ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id) VALUES
  ('ROLE_USER', 100000),
  ('ROLE_ADMIN', 100001);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2016-09-25 07:00:00', 'User breakfast first', 510, 100000);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2016-09-25 09:00:00', 'User breakfast second', 520, 100000);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2016-09-25 16:00:00', 'User dinner first', 530, 100000);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2016-09-25 07:30:00', 'Admin breakfast first', 1010, 100001);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2016-09-25 09:30:00', 'Admin breakfast second', 1020, 100001);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2016-09-25 16:30:00', 'Admin dinner first', 1030, 100001);


