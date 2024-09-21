package com.example.memo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class PDFResponseDTO {
  
  // Getters and setters
  private Long pdfId;
  private String summary;
  private String fullScript;
  private String pdfTitle;
  private String memberEmail;
  private Date documentDate;
  private String categoryName;
  private String filter;
  private Boolean isPublished;
  private Long viewCount;
  private String thumbnailUrl;       // thumbnail_url 추가
  
  // Constructor
  public PDFResponseDTO(Long pdfId, String summary, String fullScript, String pdfTitle, String memberEmail,
                        Date documentDate, String categoryName, String filter, Boolean isPublished,
                        Long viewCount, String thumbnailUrl) {  // thumbnailUrl 추가
    this.pdfId = pdfId;
    this.summary = summary;
    this.fullScript = fullScript;
    this.pdfTitle = pdfTitle;
    this.memberEmail = memberEmail;
    this.documentDate = documentDate;
    this.categoryName = categoryName != null ? categoryName : "";
    this.filter = filter != null ? filter : "";
    this.isPublished = isPublished != null ? isPublished : false;
    this.viewCount = viewCount != null ? viewCount : 0L;
    this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/PDF_file_icon.svg/640px-PDF_file_icon.svg.png"; // null일 경우 빈 문자열 처리
  }
}
