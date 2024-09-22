package com.example.memo.controller;

import com.example.memo.dto.GPTQuestionDto;
import com.example.memo.dto.PDFQuestionDto;
import com.example.memo.entity.PDFQuestionEntity;
import com.example.memo.service.GPTQuestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  @PostMapping("/askPDF")
  @CrossOrigin("*")
  public String askPDFQuestion(@Valid @RequestBody PDFQuestionDto pdfQuestionDto) throws Exception {
    // 검증된 PDF 질문을 GPT 서비스로 보내기
    return gptQuestionService.askPDFQuestion(pdfQuestionDto);
  }
  
  // 새로운 엔드포인트: PDF 질문과 답변을 DB에서 가져오기
  @PostMapping("/pdf-questions")
  @CrossOrigin("*")
  public List<PDFQuestionEntity> getPDFQuestionsAndAnswers(@RequestBody PDFQuestionDto pdfQuestionDto) {
    // 입력 값 검증
    if (pdfQuestionDto.getMemberEmail() == null || pdfQuestionDto.getPdfTitle() == null) {
      throw new IllegalArgumentException("MemberEmail and PdfTitle must be provided.");
    }
    
    // memberEmail과 pdfTitle을 기반으로 DB에서 PDF 질문과 답변 기록을 조회하여 반환
    return gptQuestionService.getPDFQuestionsAndAnswers(pdfQuestionDto.getMemberEmail(), pdfQuestionDto.getPdfTitle());
  }
  
}