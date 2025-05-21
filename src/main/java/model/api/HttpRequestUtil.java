package model.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpRequestUtil {
	
	// API call link
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    public static String get(String pathWithQuery) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + pathWithQuery))	// Compose the full URL
            .header("accept", "application/json")       // return type JSON
            .build();
        
        // send HTTP request
        HttpResponse<String> resp = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
        
        // Server return
        return resp.body();             
    }
}
