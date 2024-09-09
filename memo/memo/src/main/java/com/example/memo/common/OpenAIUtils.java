package com.example.memo.common;

import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAIUtils {
  
  @Value("${openai.api.key}")
  private String apiKey;
  
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private static final int MAX_TOKENS = 8000; // GPT-3.5-turbo의 최대 토큰 수를 고려하여 보수적으로 설정
  private static final int MAX_RESPONSE_TOKENS = 3000; // GPT-3.5-turbo의 최대 토큰 수를 고려하여 보수적으로 설정
  
  // 자막과 언어 정보를 받아 GPT-3.5-turbo를 통해 요약본을 생성하는 메소드
  public String summarizeTranscript(String transcript, String language) throws IOException {
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    
    List<String> transcriptParts = splitText(transcript);
    
    StringBuilder summaryBuilder = new StringBuilder();
    
    for (String part : transcriptParts) {
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
      userMessage.put("content", part);
      
      messagesArray.put(systemMessage);
      messagesArray.put(userMessage);
      
      json.put("messages", messagesArray);
      
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
        summaryBuilder.append(parseResponse(responseBody)).append("\n\n");
      } else {
        System.err.println("OpenAI API request failed: " + responseBody);
      }
    }
    
    return summaryBuilder.toString().trim();
  }
  
  /**
   * GPT-3.5-turbo에게 질문을 보내고 답변을 받는 메소드
   *
   * @param question 질문 내용
   * @return GPT-3.5-turbo의 답변
   * @throws IOException API 요청 실패 시
   */
  public String askQuestion(String question) throws IOException {
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    
    JSONObject json = new JSONObject();
    json.put("model", "gpt-3.5-turbo");
    
    // 사용자 메시지 추가
    JSONObject message = new JSONObject();
    message.put("role", "user");
    message.put("content", question);
    
    json.put("messages", new JSONArray().put(message));
    json.put("max_tokens", MAX_RESPONSE_TOKENS);
    
    RequestBody body = RequestBody.create(mediaType, json.toString());
    Request request = new Request.Builder()
            .url(API_URL)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + apiKey)
            .build();
    
    Response response = client.newCall(request).execute();
    assert response.body() != null;
    String responseBody = response.body().string();
    
    if (response.isSuccessful()) {
      return parseResponse(responseBody);
    } else {
      throw new IOException("OpenAI API request failed: " + responseBody);
    }
  }
  
  // PDF 문서의 텍스트와 언어 정보를 받아 GPT-3.5-turbo를 통해 요약본을 생성하는 메소드
  public String summarizePDF(String text, String language) throws IOException {
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    
    System.out.println("text = " + text);
    
    // 긴 텍스트를 나누어 처리할 부분 리스트를 생성
    List<String> textParts = splitText(text);
    
    // 요약 결과를 저장할 StringBuilder 생성
    StringBuilder finalSummary = new StringBuilder();
    
    for (String part : textParts) {
      // 각 부분에 대해 GPT-3.5-turbo에 요청을 보낼 JSON 객체 생성
      JSONObject json = new JSONObject();
      json.put("model", "gpt-3.5-turbo");
      
      JSONArray messagesArray = new JSONArray();
      
      // 시스템 메시지 추가
      JSONObject systemMessage = new JSONObject();
      systemMessage.put("role", "system");
      
      // 프롬프트 생성: 페이지별 요약 요청
      String prompt = "This text was extracted from a PDF document written in " + language + ". "
              + "Please summarize the content of each page in " + language + ". Indicate the page number at the beginning of each summary, like 'Page 1:', 'Page 2:', and so on. "
              + "Please ensure that the summaries are written in " + language + " and provide detailed and thorough explanations for each point.";
      
      systemMessage.put("content", prompt);
      
      // 사용자 메시지: 현재 처리할 부분의 텍스트
      JSONObject userMessage = new JSONObject();
      userMessage.put("role", "user");
      userMessage.put("content", part);
      
      // 메시지 배열에 시스템 메시지와 사용자 메시지 추가
      messagesArray.put(systemMessage);
      messagesArray.put(userMessage);
      
      json.put("messages", messagesArray);
      json.put("max_tokens", MAX_RESPONSE_TOKENS); // 응답의 최대 토큰 수 설정
      
      RequestBody body = RequestBody.create(mediaType, json.toString());
      Request request = new Request.Builder()
              .url(API_URL)
              .post(body)
              .addHeader("Content-Type", "application/json")
              .addHeader("Authorization", "Bearer " + apiKey)
              .build();
      
      // 요청 실행 및 응답 처리
      Response response = client.newCall(request).execute();
      String responseBody = response.body().string();
      
      if (response.isSuccessful()) {
        // 응답을 파싱하여 요약 결과를 StringBuilder에 추가
        finalSummary.append(parseResponse(responseBody)).append("\n\n");
      } else {
        System.err.println("OpenAI API request failed: " + responseBody);
      }
    }
    
    // 모든 부분 요약을 합쳐서 반환
    return finalSummary.toString().trim();
  }
  
  
  
  
  
  // OpenAI API 응답 파싱 메소드
  private String parseResponse(String response) {
    JSONObject jsonResponse = new JSONObject(response);
    return jsonResponse.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim();
  }
  
  // 자막을 토큰 수에 맞게 나누는 메소드
  private List<String> splitText(String transcript) {
    List<String> parts = new ArrayList<>();
    StringBuilder currentPart = new StringBuilder();
    int currentTokenCount = 0;
    
    // 자막을 문장별로 분리
    String[] sentences = transcript.split("(?<=[.!?])\\s*");
    
    for (String sentence : sentences) {
      int sentenceTokenCount = countTokens(sentence);
      
      // 현재 파트의 토큰 수와 문장 토큰 수를 더한 값이 최대 토큰 수를 초과하면
      if (currentTokenCount + sentenceTokenCount > MAX_TOKENS) {
        // 현재 파트가 비어있지 않으면 파트를 저장하고 리셋
        if (currentPart.length() > 0) {
          parts.add(currentPart.toString().trim());
          currentPart.setLength(0);
          currentTokenCount = 0;
        }
      }
      
      currentPart.append(sentence).append(" ");
      currentTokenCount += sentenceTokenCount;
    }
    
    // 마지막 파트가 비어있지 않으면 추가
    if (currentPart.length() > 0) {
      parts.add(currentPart.toString().trim());
    }
    
    return parts;
  }
  
  // 토큰 수를 추정하는 메소드
  private int countTokens(String text) {
    // 텍스트의 길이를 기준으로 토큰 수를 추정
    // 영어 기준: 약 1단어당 1.33 토큰, 한국어 기준: 약 1문자당 1.33 토큰
    return (int) (text.length() / 1.33);
  }
}
