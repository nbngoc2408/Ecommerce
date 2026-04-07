-- Password are in the format: Password<UserLetter>123. unless specified otherwise.
-- Encrypt it by using link: https://www.javainuse.com/onlineBcrypt
INSERT INTO customer (first_name, last_name, email, password, email_verified)
    VALUES ('UserA-firstname', 'UserA-lastname', 'userA@junittest.com', '$2a$10$hGAy3I4Jo0AQwK6OGNguTOf86NN1uGr8wujeIqwvl6/BCUdV8GMja', true)
         , ('UserB-firstname', 'UserB-lastname', 'userB@junittest.com', '$2a$10$5kK3Vo7FkrMT8TNGDPdaaOW808K7xNTLzgu0A0YaOI5UCLvY5b9dS', false);