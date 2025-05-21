package service;

import java.sql.SQLException;
import java.util.List;

import dao.MovieSystemDAOImpl;
import exception.DataAccessRuntimeException;
import exception.MovieNotFoundException;
import model.api.MovieItem;

public class MovieSystemService {
	
	private MovieSystemDAOImpl movieDao = new MovieSystemDAOImpl();
	
	// Search movie by title
	 public List<MovieItem> getMovieByTitle(String movie) {
		 try {
			var list = movieDao.findMovieByTitle(movie);
			if (list.isEmpty()) {
				throw new MovieNotFoundException(movie);
			}
			return list;
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("搜尋時發生問題", e);
		} 
	 }
	 
	 // Search Top10 popularity movie
	 public List<MovieItem> getTop10ByPopularity() {
		 try {
			return movieDao.findTopByPopularity(10);
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("排行榜搜尋時發生問題", e);
		}
    }
	 
	 // Search Top10 VoteAverage movie
	 public List<MovieItem> getTop10ByVoteAverage() {
		 try {
			return movieDao.findTopByVoteAverage(10);
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("排行榜搜尋時發生問題", e);
		}
    }
	 
	 public List<MovieItem> getAllMovies() {
		 try {
			return movieDao.findAllMovies();
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("排行榜搜尋時發生問題", e);
		}
	 }	 
}
