package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import com.example.memo.dto.GPTQuestionDto;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GPTQuestionService {
  
  private final OpenAIUtils openAIUtils;
  private static final String FLASK_API_URL = "http://localhost:8080/api/v1/questions/fetch-from-flask";
  
  public GPTQuestionService(OpenAIUtils openAIUtils) {
    this.openAIUtils = openAIUtils;
  }
  
  /**
   * GPT API를 호출하여 질문에 대한 답변을 받고 해당 정보를 Flask API로 전송하는 메소드
   *
   * @param gptQuestionDto 질문 정보
   * @return GPT API의 답변
   * @throws Exception API 요청 실패 시
   */
  public String askQuestion(GPTQuestionDto gptQuestionDto) throws Exception {
    String answer = openAIUtils.askQuestion(gptQuestionDto.getQuestion());
    postToFlask(gptQuestionDto.getMemberEmail(), gptQuestionDto.getVideoUrl(), gptQuestionDto.getQuestion(), answer);
    return answer;
  }
  
  private void postToFlask(String memberEmail, String videoUrl, String question, String answer) throws IOException {
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    
    // JSON 객체 생성
    JSONObject json = new JSONObject();
    json.put("memberEmail", memberEmail);
    json.put("videoUrl", videoUrl);
    json.put("question", question);
    json.put("answer", answer);
    
    // JSON 객체를 RequestBody로 변환
    RequestBody body = RequestBody.create(mediaType, json.toString());
    
    // POST 요청 생성
    Request request = new Request.Builder()
            .url(FLASK_API_URL)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build();
    
    // 요청 실행 및 응답 처리
    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) {
      throw new IOException("Failed to send data to Flask API: " + response);
    }
  }
}
