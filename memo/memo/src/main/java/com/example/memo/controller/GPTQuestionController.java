package com.example.memo.controller;

import com.example.memo.dto.GPTQuestionDto;
import com.example.memo.service.GPTQuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
public class GPTQuestionController {
  
  private final GPTQuestionService gptQuestionService;
  
  public GPTQuestionController(GPTQuestionService gptQuestionService) {
    this.gptQuestionService = gptQuestionService;
  }
  
  @PostMapping("/ask")
  @CrossOrigin("*")
  public String askGPTQuestion(@RequestBody GPTQuestionDto gptQuestionDto) throws Exception {
    // 입력 값 검증
    if (gptQuestionDto.getMemberEmail() == null || gptQuestionDto.getVideoUrl() == null || gptQuestionDto.getQuestion() == null) {
      throw new IllegalArgumentException("MemberEmail, VideoUrl, and Question must be provided.");
    }
    
    // 로그 찍기
    System.out.println("askGPTQuestion Request Received: " + gptQuestionDto.getMemberEmail());
    
    // GPT에게 질문을 보내고 답변을 받음
    return gptQuestionService.askQuestion(gptQuestionDto);
  }
}
