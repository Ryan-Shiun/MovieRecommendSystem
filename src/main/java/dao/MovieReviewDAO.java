package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.javaBean.MovieReview;
import utils.ConnectionFactory;

public class MovieReviewDAO {
	
	// check review exist
	public boolean exists(int userId, int movieId) throws SQLException {
		// 尋找有沒有至少一筆已經存在的評論
        String sql = "SELECT 1 FROM MovieReview WHERE user_id = ? AND movie_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
	
	// add private review
	public void postReview(int userId, int movieId, int rating, String comment) throws SQLException {

        String userSql = "SELECT user_name FROM AppUser WHERE user_id = ?";

        String movieSql = "SELECT title, popularity, vote_average FROM movieItem WHERE movie_id = ?";
     
        String insertSql = "INSERT INTO MovieReview (user_id, user_name, movie_id, title, popularity, vote_average, user_rating, comment, is_public) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
       
        
        /* -------------------------- 會員資料核對 -------------------------- */
        try (Connection conn = ConnectionFactory.getConnection()) {
        	// use batch
            conn.setAutoCommit(false);
            String userName;
            try (PreparedStatement psUser = conn.prepareStatement(userSql)) {
                psUser.setInt(1, userId);
                try (ResultSet rs = psUser.executeQuery()) {
                    if (!rs.next()) throw new SQLException("User not found: " + userId);
                    userName = rs.getString("user_name");
                }
            }
            
            /* -------------------------- 電影搜尋 --------------------------  */
            String title;
            double popularity;
            double voteAvg;
            try (PreparedStatement psMovie = conn.prepareStatement(movieSql)) {
                psMovie.setInt(1, movieId);
                
                try (ResultSet rs = psMovie.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Movie not found: " + movieId);
                    title = rs.getString("title");
                    popularity = rs.getDouble("popularity");
                    voteAvg    = rs.getDouble("vote_average");
                }
            }
            
            /* -------------------------- 加入評論 -------------------------- */
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, userId);
                ps.setString(2, userName);
                ps.setInt(3, movieId);
                ps.setString(4, title);
                ps.setDouble(5, popularity);
                ps.setDouble(6, voteAvg);
                ps.setInt(7, rating);
                ps.setString(8, comment);
                ps.setBoolean(9, false);  // true = public, false = private
                ps.executeUpdate();
            }

            conn.commit();
        }
    }
	
	// search public review by movie name
	public List<MovieReview> findReviewByTitle(Integer user_id, String movie) throws SQLException {
		List<MovieReview> reviews = new ArrayList<>();
	    String sql = "SELECT user_id, user_name, movie_id, title, popularity, vote_average, user_rating, comment, post_at, is_public"
	    		+ " FROM movieReview WHERE  user_id = ? AND title = ? AND is_public = 1";
	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	    	ps.setInt(1, user_id);
	        ps.setString(2, movie);	    
	        
	        try (ResultSet rs = ps.executeQuery()) {
	        	while (rs.next()) {
	                reviews.add(new MovieReview(
	                    rs.getInt("user_id"),
	                    rs.getString("user_name"),
	                    rs.getInt("movie_id"),
	                    rs.getString("title"),
	                    rs.getDouble("popularity"),
	                    rs.getDouble("vote_average"),
	                    rs.getInt("user_rating"),
	                    rs.getString("comment"),
	                    rs.getDate("post_at").toLocalDate().atStartOfDay(), // get from DB
	                    rs.getBoolean("is_public")
                    ));
	            }
	        }
	    } 
	    return reviews;
	}
	
	// search private review by movie name
	public List<MovieReview> findPrivateReview (Integer userId, String movie) throws SQLException {
		List<MovieReview> reviews = new ArrayList<>();
	    String sql = "SELECT user_id, user_name, movie_id, title, popularity, vote_average, user_rating, comment, post_at, is_public"
	    		+ " FROM movieReview WHERE title = ? AND user_id =? AND is_public = 0";
	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, movie);	    
	        ps.setInt(2, userId);
	        try (ResultSet rs = ps.executeQuery()) {
	        	while (rs.next()) {
	                reviews.add(new MovieReview(
	                    rs.getInt("user_id"),
	                    rs.getString("user_name"),
	                    rs.getInt("movie_id"),
	                    rs.getString("title"),
	                    rs.getDouble("popularity"),
	                    rs.getDouble("vote_average"),
	                    rs.getInt("user_rating"),
	                    rs.getString("comment"),
	                    rs.getDate("post_at").toLocalDate().atStartOfDay(), 
	                    rs.getBoolean("is_public")
	                ));
	            }
	        }
	    } 
	    return reviews;
	}
	
	// renew private review
	public void updateReview(Integer userId, String movie, int rating, String comment) throws SQLException {
	    String sql = "UPDATE MovieReview SET user_rating = ?, comment = ? " +
	                 "WHERE user_id = ? AND title = ? AND is_public = 0";
	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, rating);
	        ps.setString(2, comment);
	        ps.setInt(3, userId);
	        ps.setString(4, movie);
	        ps.executeUpdate();
	    }
    }
	
	// delete review
	public void deleteRecommendation(int userId, int movieId) throws SQLException {
        String sql = "DELETE FROM MovieReview WHERE user_id = ? AND movie_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            ps.executeUpdate();
        }
    }
	
	// search public review can Sort by popularity or user_rating
	public List<MovieReview> findAllPublic(String orderBy) throws SQLException {
        String sql = "SELECT user_id, user_name, movie_id, title, popularity, vote_average, user_rating, comment, post_at, is_public " 
        			+" FROM MovieReview WHERE is_public = 1 ORDER BY " + orderBy + " DESC";                    
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<MovieReview> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new MovieReview(
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getDouble("popularity"),  
                    rs.getDouble("vote_average"),
                    rs.getInt("user_rating"),
                    rs.getString("comment"),
                    rs.getDate("post_at").toLocalDate().atStartOfDay(), // 由資料庫填入 DATE
                    rs.getBoolean("is_public")
                ));
            }
            return list;
        }
    }
	
	// search private review by user ID
	public List<MovieReview> findAllPrivate(Integer userId) throws SQLException {
        String sql = "SELECT user_id, user_name, movie_id, title, popularity, vote_average, user_rating, comment, post_at, is_public " 
        			+" FROM MovieReview WHERE user_id = ? AND is_public = 0";                    
        
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
           	ps.setInt(1, userId);
               try (ResultSet rs = ps.executeQuery()) {
                   List<MovieReview> list = new ArrayList<>();
                   while (rs.next()) {
                       list.add(new MovieReview(
                           rs.getInt("user_id"),
                           rs.getString("user_name"),
                           rs.getInt("movie_id"),
                           rs.getString("title"),
                           rs.getDouble("popularity"),
                           rs.getDouble("vote_average"),
                           rs.getInt("user_rating"),
                           rs.getString("comment"),
                           rs.getDate("post_at").toLocalDate().atStartOfDay(),
                           rs.getBoolean("is_public") 
                       ));
                   }
                   return list;
               }
        }
    }
	
	// search public review by user name
	public List<MovieReview> findByName(String userName) throws SQLException {
        String sql = 
                "SELECT user_id, user_name, movie_id, title, popularity, vote_average, " +
                "       user_rating, comment, post_at, is_public " +
                "  FROM MovieReview " +
                " WHERE user_name = ? " +
                "   AND is_public = 1 " +
                " ORDER BY post_at DESC";
                     
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                List<MovieReview> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new MovieReview(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getDouble("popularity"),
                        rs.getDouble("vote_average"),
                        rs.getInt("user_rating"),
                        rs.getString("comment"),
                        rs.getDate("post_at").toLocalDate().atStartOfDay(),
                        rs.getBoolean("is_public") 
                    ));
                }
                return list;
            }
        }
    }
	
	// post review to public
	public void pubReview(int userId, int movieId ) throws SQLException {
        String sql = "UPDATE MovieReview SET is_public = 1 WHERE user_id = ? AND movie_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            ps.executeUpdate();
        }
    }
}
