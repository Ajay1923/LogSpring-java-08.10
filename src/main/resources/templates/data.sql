INSERT INTO users(username, password) VALUES ('oslo', 'oslo');
INSERT INTO users(username, password) VALUES ('user', 'user');

SELECT * FROM users WHERE username = 'oslo';
SELECT * FROM users WHERE username ='user';
