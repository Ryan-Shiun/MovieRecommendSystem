package model.api.test;

import java.util.List;

import model.api.HttpRequestUtil;
import model.api.MovieItem;
import model.api.TmdbParser;

public class Test  {
	public static void main(String[] args) {
        try {
            String apiKey = "62404e7c912683774616519718e88444";
            String query  = "/trending/movie/day?api_key=" + apiKey + "&language=zh-TW";

            String json = HttpRequestUtil.get(query);   // 第 4 步
            List<MovieItem> list = new TmdbParser().parseTrending(json); // 第 5 步

            list.forEach(m -> System.out.printf("%d. %s (%.1f)%n",
                                                m.id(), m.title(), m.voteAverage()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
