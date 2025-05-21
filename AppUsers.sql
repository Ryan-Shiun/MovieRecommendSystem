CREATE TABLE AppUser (
    user_id     INT IDENTITY	PRIMARY KEY,
    user_name   NVARCHAR(50)	UNIQUE NOT NULL,		
    email       NVARCHAR(100)	UNIQUE NOT NULL,
	password    NVARCHAR(100)	NOT NULL,
	createdAt	DATE DEFAULT	GETDATE()
);
SELECT * FROM Appuser


TRUNCATE TABLE Appuser;
--DROP TABLE Appuser




