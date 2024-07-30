package com.example.memo.controller;

import com.example.memo.dto.YoutubeRequestDto;
import com.example.memo.dto.YoutubeResponseDto;
import com.example.memo.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/video")
public class YoutubeController {
  
  @Autowired
  private YoutubeService youtubeService;
  
  @PostMapping("/get-full-script")
  @CrossOrigin("*")
  public ResponseEntity<?> getFullScript(@RequestBody YoutubeRequestDto requestDto) {
    try {
      // 요청 데이터 콘솔에 출력
      System.out.println("Received URL for script: " + requestDto.getUrl());
      System.out.println("Received memberEmail: " + requestDto.getMemberEmail());
      
      // 이메일 검증
      if (requestDto.getMemberEmail() == null || requestDto.getMemberEmail().isEmpty()) {
        return new ResponseEntity<>("이메일을 확인해주세요.", HttpStatus.BAD_REQUEST);
      }
      
      // 유튜브 URL 처리 및 DTO 반환
      YoutubeResponseDto responseDto = youtubeService.processYoutubeUrl(requestDto.getUrl(), requestDto.getMemberEmail());
      return new ResponseEntity<>(responseDto, HttpStatus.OK);
      
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("Error processing the request.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
