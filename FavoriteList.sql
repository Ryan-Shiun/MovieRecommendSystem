CREATE TABLE MovieReview (
    user_id       INT             NOT NULL,                                   
    user_name     NVARCHAR(50)    NOT NULL,                                    
    movie_id      INT             NOT NULL,                                    
    title         NVARCHAR(255)   NOT NULL,                                    
    popularity    FLOAT           NULL,                                        
    vote_average  FLOAT           NULL,                                       
    user_rating   INT             NULL,                                       
    comment       NVARCHAR(MAX)   NULL,                                       
    post_at		  DATE			  NOT NULL DEFAULT GETDATE(),
	is_public	  BIT			  NOT NULL DEFAULT 1 -- 1 = public, 0 = private
);

SELECT * FROM MovieReview


TRUNCATE TABLE MovieReview;

SELECT user_id, user_name, movie_id, title, popularity, vote_average, user_rating, comment, post_at, is_public 
 FROM MovieReview WHERE user_name = 'ryan' AND is_public = 1  ORDER BY post_at DESC



--DROP TABLE MovieReview;

-- 使用者外鍵約束：user_id 對應 AppUser
ALTER TABLE dbo.MovieReview
ADD CONSTRAINT FK_Review_User
    FOREIGN KEY (user_id)
    REFERENCES dbo.AppUser(user_id);

-- 電影外鍵約束：movie_id 對應 movieItem
ALTER TABLE dbo.MovieReview
ADD CONSTRAINT FK_Review_Movie
    FOREIGN KEY (movie_id)
    REFERENCES dbo.movieItem(movie_id);

-- 雙欄 PK 確保同個 User 只能評論同個電影一次
ALTER TABLE dbo.MovieReview
ADD CONSTRAINT PK_MovieReview PRIMARY KEY (user_id, movie_id);

ALTER TABLE dbo.MovieReview
ADD is_public BIT NOT NULL
    CONSTRAINT DF_MovieReview_IsPublic DEFAULT 1;

UPDATE dbo.MovieReview
SET is_public = CASE WHEN visibility = 'Y' THEN 1 ELSE 0 END;

ALTER TABLE dbo.MovieReview
DROP COLUMN visibility;