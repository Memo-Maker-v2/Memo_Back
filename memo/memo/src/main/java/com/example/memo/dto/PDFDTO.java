package com.example.memo.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity // JPA 엔티티임을 명시
@Table(name = "pdf_table") // 데이터베이스 테이블 이름을 명시
@Data // 롬복을 이용해 Getter, Setter, toString, EqualsAndHashCode 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
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
}
