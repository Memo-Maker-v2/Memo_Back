package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import org.springframework.stereotype.Service;

@Service
public class GPTQuestionService {
  
  private final OpenAIUtils openAIUtils;
  
  public GPTQuestionService(OpenAIUtils openAIUtils) {
    this.openAIUtils = openAIUtils;
  }
  
  /**
   * GPT API를 호출하여 질문에 대한 답변을 받는 메소드
   *
   * @param question 질문 내용
   * @return GPT API의 답변
   * @throws Exception API 요청 실패 시
   */
  public String askQuestion(String question) throws Exception {
    
    return openAIUtils.askQuestion(question);
  }
}
