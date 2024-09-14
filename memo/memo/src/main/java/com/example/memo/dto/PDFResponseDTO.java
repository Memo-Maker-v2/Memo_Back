package com.example.memo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class PDFResponseDTO {
  
  // Getters and setters
  private Long pdfId;                // pdfId 추가
  private String summary;
  private String fullScript;
  private String pdfTitle;
  private String memberEmail;
  private Date documentDate;
  private String categoryName;       // category_name 추가
  private String filter;             // filter 추가
  private Boolean isPublished;       // is_published 추가
  private Long viewCount;            // view_count 추가
  
  // Constructor
  public PDFResponseDTO(Long pdfId, String summary, String fullScript, String pdfTitle, String memberEmail,
                        Date documentDate, String categoryName, String filter, Boolean isPublished, Long viewCount) {
    this.pdfId = pdfId;
    this.summary = summary;
    this.fullScript = fullScript;
    this.pdfTitle = pdfTitle;
    this.memberEmail = memberEmail;
    this.documentDate = documentDate;
    this.categoryName = categoryName != null ? categoryName : ""; // null일 경우 빈 문자열로 처리
    this.filter = filter != null ? filter : "";                   // null일 경우 빈 문자열로 처리
    this.isPublished = isPublished != null ? isPublished : false; // null일 경우 기본값 false
    this.viewCount = viewCount != null ? viewCount : 0L;          // null일 경우 기본값 0L
  }
}
