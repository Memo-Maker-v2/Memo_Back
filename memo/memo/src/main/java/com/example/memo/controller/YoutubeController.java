package com.example.memo.controller;

import com.example.memo.dto.YoutubeRequestDto;
import com.example.memo.dto.YoutubeResponseDto;
import com.example.memo.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/video")
public class YoutubeController {
  
  @Autowired
  private YoutubeService youtubeService;
  
  @PostMapping("/get-full-script")
  @CrossOrigin("*")
  public YoutubeResponseDto getFullScript(@RequestBody YoutubeRequestDto requestDto) {
    try {
      // 요청 데이터 콘솔에 출력
      System.out.println("Received URL for script: " + requestDto.getUrl());
      System.out.println("Received memberEmail: " + requestDto.getMemberEmail());
      
      // 유튜브 URL 처리 및 DTO 반환
      return youtubeService.processYoutubeUrl(requestDto.getUrl(), requestDto.getMemberEmail());
      
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error processing the request.");
    }
  }
}
