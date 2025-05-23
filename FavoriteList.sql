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

--DROP TABLE MovieReview;

-- �ϥΪ̥~������Guser_id ���� AppUser
ALTER TABLE dbo.MovieReview
ADD CONSTRAINT FK_Review_User
    FOREIGN KEY (user_id)
    REFERENCES dbo.AppUser(user_id);

-- �q�v�~������Gmovie_id ���� movieItem
ALTER TABLE dbo.MovieReview
ADD CONSTRAINT FK_Review_Movie
    FOREIGN KEY (movie_id)
    REFERENCES dbo.movieItem(movie_id);

-- ���� PK �T�O�P�� User �u����צP�ӹq�v�@��
ALTER TABLE dbo.MovieReview
ADD CONSTRAINT PK_MovieReview PRIMARY KEY (user_id, movie_id);




