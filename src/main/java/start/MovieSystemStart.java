package start;

import java.util.List;

import javax.swing.SwingUtilities;

import dao.MovieSystemDAOImpl;
import frame.MainFrame;
import model.api.HttpRequestUtil;
import model.api.MovieItem;
import model.api.TmdbParser;

public class MovieSystemStart {

	public static void main(String[] args) {
		try {
            // API key
            String apiKey = "62404e7c912683774616519718e88444"; 

            //  API path
            String apiPath = "/trending/movie/day?api_key=" + apiKey + "&language=zh-TW";

            // call API get data
            String json = HttpRequestUtil.get(apiPath);
            System.out.println("TMDB 取得 JSON 資料");

            // JSON → List<MovieItem>
            List<MovieItem> movies = new TmdbParser().parseTrending(json);
            System.out.println("成功轉換電影筆數：" + movies.size());
                        
            // write into DB
            new MovieSystemDAOImpl().upsert(movies);
            System.out.println("已寫入資料庫！");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("錯誤：無法成功執行 API → DB 流程");
        }		
		
		// start GUI
		SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
	}

}
