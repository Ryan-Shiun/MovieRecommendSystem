package service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import dao.MovieReviewDAO;
import exception.DataAccessRuntimeException;
import exception.ReviewAlreadyExistsException;
import exception.ReviewNotFoundException;
import model.javaBean.MovieReview;

public class MovieReviewService {
	
	private MovieReviewDAO dao = new MovieReviewDAO();
	
	// get public review
	public List<MovieReview> getPublicReviw (Integer userId, String title) {

		try {
			var list = dao.findReviewByTitle(userId, title);
			return list;
			
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("讀取評論時發生問題", e);
		}
	}
	
	// get all private review
	public List<MovieReview> findAllPrivate(int userId) {		
		try {
			return dao.findAllPrivate(userId);			
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("讀取評論時發生問題", e);
		}

	}
	
	// get public review by user name
	public List<MovieReview> searchByName(String userName) {
				
        try {
        	var list = dao.findByName(userName);
            if (list.isEmpty()) {
            	throw new ReviewNotFoundException();
            }
            return list;
        } catch (SQLException e) {
        	throw new DataAccessRuntimeException("查詢評論時發生問題", e);
        }
    }
	
	// get all private review 
	public List<MovieReview> getPrivewReview (Integer userId, String title) {		
		try {
			var list = dao.findPrivateReview(userId, title);
			if (list.isEmpty()) {
				throw new ReviewNotFoundException();
			}
			return list;
			
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("讀取評論時發生問題", e);
		}
	}
	
	// sort public review
	public List<MovieReview> findAllPublic(String orderBy) {
		
		try {
			return dao.findAllPublic(orderBy);			
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("讀取評論時發生問題", e);
		}
    }
	
	
	// add private review
	public void postReview(int userId, int movieId, int rating, String comment) { 		
		try {
			if (dao.exists(userId, movieId)) {
				throw new ReviewAlreadyExistsException();
			}
			dao.postReview(userId, movieId, rating, comment);
			
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("新增評論時發生問題", e);
		}			
    }
	
	// renew private review
	public void updateReview(int userId, String movie, int rating, String comment) {	
        try {
			var list = dao.findPrivateReview(userId, movie);
			if (list.isEmpty()) {
				throw new ReviewNotFoundException();
			}        	
			dao.updateReview(userId, movie, rating, comment);
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("更新評論時發生問題", e);
		}
    }
	
	// delete private renew
	public void deleteRecommendation(int userId, int movieId) {
        try {
            dao.deleteRecommendation(userId, movieId);
        } catch (SQLException e) {
            throw new ReviewNotFoundException();
        }
    }
		
	// post review to public
	public void publicReview(int userId, int movieId) {
        try {
        	dao.pubReview(userId, movieId);           
        } catch (SQLException e) {
            throw new DataAccessRuntimeException("發表評論時發生問題", e);
        }
    }
	
	// export private review to CSV file
	public void exportCsv(List<MovieReview> list, String outputPath) throws IOException {
	    CSVFormat format = CSVFormat.DEFAULT
	        .builder()
	        .setHeader("title", "popularity", "voteAverage", "userRating", "comment")
	        .setSkipHeaderRecord(false)
	        .build();

	    try (
	        FileOutputStream fos = new FileOutputStream(outputPath);
	        OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)
	    ) {
	        writer.write('\uFEFF');  //  加上 BOM，讓 Excel 能讀 UTF-8

	        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
	            for (MovieReview m : list) {
	                printer.printRecord(
	                    m.title(),
	                    m.popularity(),
	                    m.voteAverage(),
	                    m.userRating(),
	                    m.comment()
	                );
	            }
	        }
	    } catch (IOException e) {
	        throw new IOException();
	    }
	}
}
