package com.example.memo.service;

import com.example.memo.dto.PDFQuestionDto;
import com.example.memo.entity.PDFQuestionEntity;
import com.example.memo.repository.PDFQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PDFQuestionService {
  
  private final PDFQuestionRepository pdfQuestionRepository;
  
  public PDFQuestionService(PDFQuestionRepository pdfQuestionRepository) {
    this.pdfQuestionRepository = pdfQuestionRepository;
  }
  
  @Transactional
  public PDFQuestionEntity savePDFQuestion(PDFQuestionDto pdfQuestionDto, String answer) throws Exception {
    String memberEmail = pdfQuestionDto.getMemberEmail();
    if (memberEmail == null) {
      throw new IllegalArgumentException("Member email is missing in the request.");
    }
    
    PDFQuestionEntity pdfQuestionEntity = new PDFQuestionEntity();
    pdfQuestionEntity.setQuestion(pdfQuestionDto.getQuestion());
    pdfQuestionEntity.setAnswer(answer);
    pdfQuestionEntity.setMemberEmail(memberEmail);
    pdfQuestionEntity.setPdfTitle(pdfQuestionDto.getPdfTitle());
    
    return pdfQuestionRepository.save(pdfQuestionEntity);
  }
}
