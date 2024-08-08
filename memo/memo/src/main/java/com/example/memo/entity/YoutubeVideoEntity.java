package com.example.memo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "video_table")
public class YoutubeVideoEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  
  /*gpt 동영상요약내용*/
  @Column(name = "summary")
  private String summary;
  
  /*전체 스크립트*/
  @Column(name = "full_script")
  private String fullScript;
  
  /*유튜브영상 url*/
  @Column(name = "video_url")
  private String videoUrl;
  
  /*썸네일 url*/
  @Column(name = "thumbnail_url")
  private String thumbnailUrl;
  
  /*유튜브영상 제목*/
  @Column(name = "video_title")
  private String videoTitle;
  
  /*사용자가 직접 추가하는 카테고리*/
  @Column(name = "category_name")
  private String categoryName;
  
  /*필터*/
  @Column
  private String filter;
  
  @Column(name = "member_email")
  private String memberEmail;
  
  @Column(name = "document_date")
  private LocalDate documentDate;
  
  public YoutubeVideoEntity() {
  }
  
  public YoutubeVideoEntity(String summary, String fullScript, String videoUrl, String thumbnailUrl, String videoTitle, String memberEmail, LocalDate documentDate) {
    this.summary = summary;
    this.fullScript = fullScript;
    this.videoUrl = videoUrl;
    this.thumbnailUrl = thumbnailUrl;
    this.videoTitle = videoTitle;
    this.memberEmail = memberEmail;
    this.documentDate = documentDate;
  }
  
}
