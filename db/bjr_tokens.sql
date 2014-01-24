CREATE TABLE bjr_tokens
(
	TokenID INT NOT NULL AUTO_INCREMENT,
	UserID INT,
	TokenStr VARCHAR(255),
	TokenIsActive INT,
	TokenLastUsed DATE,
	PRIMARY KEY (TokenID)
);