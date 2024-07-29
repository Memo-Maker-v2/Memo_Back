package com.example.memo.common;

import okhttp3.*;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAIUtils {
  
  @Value("${openai.api.key}")
  private String apiKey;
  
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  
  // 자막과 언어 정보를 받아 GPT-3.5-turbo를 통해 요약본을 생성하는 메소드
  public String summarizeTranscript(String transcript, String language) throws IOException {
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    
    // 요청 JSON 생성
    JSONObject json = new JSONObject();
    json.put("model", "gpt-3.5-turbo");
    
    JSONArray messagesArray = new JSONArray();
    
    // 시스템 메시지 추가
    JSONObject systemMessage = new JSONObject();
    systemMessage.put("role", "system");
    systemMessage.put("content", "Please analyze the following transcript and provide a summary with subheadings and brief descriptions in " + language + ". Include appropriate emojis with the subheadings and descriptions without an introduction or closing remark.");

    // 사용자 메시지 추가
    JSONObject userMessage = new JSONObject();
    userMessage.put("role", "user");
    userMessage.put("content", transcript);
    
    messagesArray.put(systemMessage);
    messagesArray.put(userMessage);
    
    json.put("messages", messagesArray);
    json.put("max_tokens", 2048);
    
    RequestBody body = RequestBody.create(mediaType, json.toString());
    Request request = new Request.Builder()
            .url(API_URL)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + apiKey)
            .build();
    
    Response response = client.newCall(request).execute();
    String responseBody = response.body().string();
    
    if (response.isSuccessful()) {
      return parseResponse(responseBody);
    } else {
      System.err.println("OpenAI API request failed: " + responseBody);
      return "error";
    }
  }
  
  // OpenAI API 응답 파싱 메소드
  private String parseResponse(String response) {
    JSONObject jsonResponse = new JSONObject(response);
    JSONArray choices = jsonResponse.getJSONArray("choices");
    JSONObject firstChoice = choices.getJSONObject(0);
    JSONObject message = firstChoice.getJSONObject("message");
    return message.getString("content").trim();
  }
}
