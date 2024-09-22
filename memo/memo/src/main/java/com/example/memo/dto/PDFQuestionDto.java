package com.example.memo.dto;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PDFQuestionDto {
  
  @NotBlank(message = "Member email is required")
  private String memberEmail;
  
  @NotBlank(message = "PDF title is required")
  private String pdfTitle;
  
  @NotBlank(message = "Question is required")
  private String question;
  
  // 기본 생성자
  public PDFQuestionDto() {}
  
  // 생성자
  public PDFQuestionDto(String memberEmail, String pdfTitle, String question) {
    this.memberEmail = memberEmail;
    this.pdfTitle = pdfTitle;
    this.question = question;
  }
}
