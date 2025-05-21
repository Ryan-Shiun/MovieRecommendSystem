CREATE TABLE movieItem (
	movie_id	 INT  PRIMARY KEY,
	title		 NVARCHAR(50) NOT NULL,
	popularity   FLOAT,
	vote_average FLOAT
);

SELECT * FROM movieItem

TRUNCATE TABLE movieItem;
-- DROP TABLE movieItem



