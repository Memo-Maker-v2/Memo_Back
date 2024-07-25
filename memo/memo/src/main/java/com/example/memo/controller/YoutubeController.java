package com.example.memo.controller;

import com.example.memo.dto.YoutubeRequestDto;
import com.example.memo.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/video")
public class YoutubeController {
  
  @Autowired
  private YoutubeService youtubeService;
  
  @PostMapping("/get-title")
  @CrossOrigin("*")
  public String getVideoTitle(@RequestBody YoutubeRequestDto requestDto) {
    try {
      // 요청 데이터 콘솔에 출력
      System.out.println("Received URL: " + requestDto.getUrl());
      
      // 비디오 제목 가져오기
      return youtubeService.getVideoTitle(requestDto.getUrl());
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error processing the request.");
    }
  }
}
