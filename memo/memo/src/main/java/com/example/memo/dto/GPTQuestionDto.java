package com.example.memo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GPTQuestionDto {
  
  private String memberEmail; // 유저 이메일
  private String videoUrl;    // 영상 URL
  private String question;    // 질문 내용
  
  // 기본 생성자
  public GPTQuestionDto() {}
  
  // 생성자
  public GPTQuestionDto(String memberEmail, String videoUrl, String question) {
    this.memberEmail = memberEmail;
    this.videoUrl = videoUrl;
    this.question = question;
  }
}
