-- Seed roles
INSERT INTO role (name) VALUES ('CUSTOMER');
INSERT INTO role (name) VALUES ('ADMIN');

-- Password are in the format: Password<UserLetter>123. unless specified otherwise.
-- Encrypt it by using link: https://www.javainuse.com/onlineBcrypt
INSERT INTO customer (first_name, last_name, email, password, email_verified)
    VALUES ('UserA-firstname', 'UserA-lastname', 'userA@junittest.com', '$2a$10$hGAy3I4Jo0AQwK6OGNguTOf86NN1uGr8wujeIqwvl6/BCUdV8GMja', true)
         , ('UserB-firstname', 'UserB-lastname', 'userB@junittest.com', '$2a$10$5kK3Vo7FkrMT8TNGDPdaaOW808K7xNTLzgu0A0YaOI5UCLvY5b9dS', false);

-- Assign CUSTOMER role to seed test users
INSERT INTO customer_role (customer_id, role_id)
    SELECT c.customer_id, r.role_id FROM customer c, role r WHERE r.name = 'CUSTOMER';