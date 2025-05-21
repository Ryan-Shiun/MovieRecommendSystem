package model.api.test;

import model.api.HttpRequestUtil;

/* 測試用 */
public class HttpRequestTest {

	public static void main(String[] args) {
        try {
        	// My API
            String apiKey = "62404e7c912683774616519718e88444";
            String query = "/trending/movie/day?api_key=" + apiKey + "&language=zh-TW";

            String result = HttpRequestUtil.get(query);
            System.out.println(result); // 看是否能印出 JSON 字串

        } catch (Exception e) {
            e.printStackTrace(); // 印出錯誤訊息
        }
    }
}
