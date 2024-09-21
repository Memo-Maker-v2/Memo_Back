package com.example.memo.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "pdf_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDFDTO {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pdf_id")
  private Long pdfId;
  
  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;
  
  @Column(name = "full_script", columnDefinition = "LONGTEXT")
  private String fullScript;
  
  @Column(name = "pdf_title", nullable = false, length = 100)
  private String pdfTitle;
  
  @Column(name = "member_email", nullable = false, length = 100)
  private String memberEmail;
  
  @Column(name = "document_date")
  @Temporal(TemporalType.DATE)
  private Date documentDate;
  
  @Column(name = "category_name", length = 45)
  private String categoryName;
  
  @Column(name = "filter", length = 45)
  private String filter;
  
  @Column(name = "is_published")
  private Boolean isPublished = false;
  
  @Column(name = "view_count")
  private Long viewCount;
  
  @Column(name = "thumbnail_url", length = 255)  // thumbnail_url 추가
  private String thumbnailUrl;
}
