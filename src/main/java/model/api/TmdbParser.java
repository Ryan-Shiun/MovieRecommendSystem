package model.api;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TmdbParser {
	
	// 如果 JSON 中有你 Java 類別中沒定義的欄位，不要報錯，直接忽略它
    private static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    // 將巢狀的 JSON 字串分層 方便後續 Java Bean 對應
    public List<MovieItem> parseTrending(String json) throws Exception {
        TrendResponse resp = mapper.readValue(json, TrendResponse.class);
        return resp.results();
    }
}
