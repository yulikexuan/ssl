 CREATE TABLE IF NOT EXISTS persistent_logins (
    username VARCHAR (64) NOT NULL,
    series VARCHAR(255) PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
);