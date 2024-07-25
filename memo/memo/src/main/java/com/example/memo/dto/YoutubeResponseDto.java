package com.example.memo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YoutubeResponseDto {
  private String title;
  private String subtitles;
  
  public YoutubeResponseDto(String title, String subtitles) {
    this.title = title;
    this.subtitles = subtitles;
  }
  
}
