package com.example.memo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YoutubeResponseDto {
  private String videoTitle;
  private String fullScript;
  private String videoUrl;
  private String thumbnailUrl;
  private String summary;
  private String memberEmail;
  private String date;
  
  public YoutubeResponseDto(String videoTitle, String fullScript, String videoUrl,
                            String thumbnailUrl, String summary, String memberEmail, String date) {
    this.videoTitle = videoTitle;
    this.fullScript = fullScript;
    this.videoUrl = videoUrl;
    this.thumbnailUrl = thumbnailUrl;
    this.summary = summary;
    this.memberEmail = memberEmail;
    this.date = date;
  }
}
