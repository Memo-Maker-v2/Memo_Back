package com.example.memo.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class YoutubeService {
  
  @Value("${youtube.api.key}")
  private String apiKey;
  
  private final OkHttpClient httpClient = new OkHttpClient();
  
  public String getVideoTitle(String videoUrl) throws IOException, JSONException {
    String videoId = extractVideoId(videoUrl);
    String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + apiKey;
    
    Request request = new Request.Builder()
            .url(apiUrl)
            .build();
    
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
      
      JSONObject jsonResponse = new JSONObject(response.body().string());
      return jsonResponse.getJSONArray("items")
              .getJSONObject(0)
              .getJSONObject("snippet")
              .getString("title");
    }
  }
  
  private String extractVideoId(String videoUrl) {
    String[] parts = videoUrl.split("v=");
    if (parts.length > 1) {
      return parts[1];
    } else {
      throw new IllegalArgumentException("Invalid YouTube URL");
    }
  }
}
