package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import com.example.memo.dto.GPTQuestionDto;
import com.example.memo.dto.PDFQuestionDto;
import com.example.memo.entity.PDFQuestionEntity;
import com.example.memo.repository.PDFQuestionRepository;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GPTQuestionService {
  
  private final OpenAIUtils openAIUtils;
  private final PDFQuestionRepository pdfQuestionRepository;
  private static final String FLASK_API_URL = "http://localhost:8080/api/v1/questions/fetch-from-flask";
  
  public GPTQuestionService(OpenAIUtils openAIUtils, PDFQuestionRepository pdfQuestionRepository) {
    this.openAIUtils = openAIUtils;
    this.pdfQuestionRepository = pdfQuestionRepository;
  }
  
  /**
   * GPT API를 호출하여 질문에 대한 답변을 받고 해당 정보를 Flask API로 전송하는 메소드
   *
   * @param gptQuestionDto 질문 정보
   * @return GPT API의 답변
   * @throws Exception API 요청 실패 시
   */
  public String askQuestion(GPTQuestionDto gptQuestionDto) throws Exception {
    try {
      System.out.println("gptQuestionDto = " + gptQuestionDto);
      String answer = openAIUtils.askQuestion(gptQuestionDto.getQuestion());
      System.out.println("answer = " + answer);
      postToFlask(gptQuestionDto.getMemberEmail(), gptQuestionDto.getVideoUrl(), gptQuestionDto.getQuestion(), answer);
      System.out.println("finish postToFlask");
      return answer;
    } catch (IOException e) {
      System.err.println("IOException during Flask API call: " + e.getMessage());
      throw e;
    } catch (Exception e) {
      System.err.println("Unexpected exception: " + e.getMessage());
      e.printStackTrace();  // stack trace를 출력합니다.
      throw e;
    }
  }
  
  private void postToFlask(String memberEmail, String videoUrl, String question, String answer) throws IOException {
    System.out.println("memberEmail = " + memberEmail);
    System.out.println("videoUrl = " + videoUrl);
    System.out.println("answer = " + answer);
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    
    JSONObject json = new JSONObject();
    json.put("memberEmail", memberEmail);
    json.put("videoUrl", videoUrl);
    json.put("question", question);
    json.put("answer", answer);
    
    RequestBody body = RequestBody.create(mediaType, json.toString());
    
    Request request = new Request.Builder()
            .url(FLASK_API_URL)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build();
    
    // 요청 및 응답 처리
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        System.err.println("Failed to send data to Flask API. Response: " + response);
        throw new IOException("Failed to send data to Flask API: " + response);
      }
      System.out.println("Successfully posted to Flask API. Response: " + response);
    } catch (IOException e) {
      System.err.println("IOException occurred while posting to Flask API: " + e.getMessage());
      throw e;
    }
  }
  
  /**
   * PDF 관련 질문에 대한 답변을 GPT API로부터 받고, 그 질문과 답변을 DB에 저장하는 메소드
   *
   * @param pdfQuestionDto PDF 질문 정보
   * @return GPT API의 답변
   * @throws Exception API 요청 실패 시
   */
  public String askPDFQuestion(PDFQuestionDto pdfQuestionDto) throws Exception {
    try {
      // GPT API에 질문을 보내고 답변을 받음
      String answer = openAIUtils.askQuestion(pdfQuestionDto.getQuestion());
      
      // 받은 답변과 함께 PDF 질문을 DB에 저장
      PDFQuestionEntity pdfQuestionEntity = new PDFQuestionEntity();
      pdfQuestionEntity.setMemberEmail(pdfQuestionDto.getMemberEmail());
      pdfQuestionEntity.setPdfTitle(pdfQuestionDto.getPdfTitle());
      pdfQuestionEntity.setQuestion(pdfQuestionDto.getQuestion());
      pdfQuestionEntity.setAnswer(answer);  // GPT로부터 받은 답변 저장
      
      // Repository를 이용해 DB에 저장
      pdfQuestionRepository.save(pdfQuestionEntity);
      
      return answer;
    } catch (IOException e) {
      System.err.println("IOException during GPT API call: " + e.getMessage());
      throw e;
    } catch (Exception e) {
      System.err.println("Unexpected exception: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }
  
}
