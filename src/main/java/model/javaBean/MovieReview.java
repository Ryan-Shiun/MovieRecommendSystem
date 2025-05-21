package model.javaBean;

import java.time.LocalDateTime;

public record MovieReview(
	    int userId,
	    String userName,
	    int movieId,
	    String title,
	    double popularity,
	    double voteAverage,
	    int userRating,
	    String comment,
	    LocalDateTime postAt,
	    boolean isPublic 
) {}
