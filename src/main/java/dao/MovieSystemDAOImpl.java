package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.api.MovieItem;
import utils.ConnectionFactory;

public class MovieSystemDAOImpl {
	
	// get API data from TMDB
	public void upsert(List<MovieItem> list) throws SQLException {
		// use merge sync database
		String sql = """
			    MERGE INTO movieItem AS tgt                          
			    USING (SELECT ?  AS movie_id,
			                  ?  AS title,
			                  ?  AS popularity,
			                  ?  AS vote_average) AS src            
			    ON (tgt.movie_id = src.movie_id)                    

			    WHEN MATCHED THEN                                   
			        UPDATE SET title        = src.title,
			                   popularity   = src.popularity,
			                   vote_average = src.vote_average

			    WHEN NOT MATCHED THEN                               
			        INSERT (movie_id, title, popularity, vote_average)
			        VALUES (src.movie_id, src.title,
			                src.popularity, src.vote_average);
			    """;
		
		try (Connection conn = ConnectionFactory.getConnection();
		         PreparedStatement ps = conn.prepareStatement(sql)) {
				// send in batches at once
		        for (MovieItem m : list) {
		            ps.setInt(1, m.id());
		            ps.setString(2, m.title());
		            ps.setDouble(3, m.popularity());
		            ps.setDouble(4, m.voteAverage());
		            ps.addBatch();
		        }
		        ps.executeBatch();
		    }
	}
	
	/*  --------------- Search movie function --------------- */
	
	// find all movies
    public List<MovieItem> findAllMovies() throws SQLException {
        List<MovieItem> movies = new ArrayList<>();
        String sql = "SELECT movie_id, title, popularity, vote_average FROM movieItem";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MovieItem item = new MovieItem(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getDouble("popularity"),
                    rs.getDouble("vote_average")
                );
                movies.add(item);
            }
        } 
        return movies;
    }
	
	// find movieId by title
	public List<MovieItem> findMovieByTitle(String movie) throws SQLException {
		List<MovieItem> movies = new ArrayList<>();
	    String sql = "SELECT movie_id, popularity, title, vote_average FROM movieItem WHERE title = ? ";
	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, movie);	    
	        
	        try (ResultSet rs = ps.executeQuery()) {
	        	while (rs.next()) {
	                MovieItem item = new MovieItem(
	                    rs.getInt("movie_id"),
	                    rs.getString("title"),
	                    rs.getDouble("popularity"),
	                    rs.getDouble("vote_average")
	                );
	                movies.add(item);
	            }
	        }
	    } 
	    return movies;
	}
	
	
	// find movie by popularity
	public List<MovieItem> findTopByPopularity(int n) throws SQLException {
		List<MovieItem> list = new ArrayList<>();
		String sql = "SELECT TOP " + n
				   + " movie_id, title, popularity, vote_average "
			       + " FROM movieItem ORDER BY popularity DESC ";
		
		try (Connection connection = ConnectionFactory.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql);
			 ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				MovieItem item = new MovieItem(
						resultSet.getInt("movie_id"),
						resultSet.getString("title"),
						resultSet.getDouble("popularity"),
						resultSet.getDouble("vote_average")							
				);
				list.add(item);
			}		
		} 
		return list;
	}
	
	// find movie by VoteAverage
	public List<MovieItem> findTopByVoteAverage(int n) throws SQLException {
		List<MovieItem> list = new ArrayList<>();
		String sql = "SELECT TOP " + n
				  + " movie_id, title, popularity, vote_average "
			      + " FROM movieItem ORDER BY vote_average DESC ";
		
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				MovieItem item = new MovieItem(
						resultSet.getInt("movie_id"),
						resultSet.getString("title"),
						resultSet.getDouble("popularity"),
						resultSet.getDouble("vote_average")							
				);
				list.add(item);
			}			
		} 
		return list;
	}
}
