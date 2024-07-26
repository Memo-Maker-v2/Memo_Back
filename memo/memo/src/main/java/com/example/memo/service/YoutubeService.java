package com.example.memo.service;

import com.example.memo.dto.YoutubeResponseDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Scanner;

@Service
public class YoutubeService {
  
  @Value("${youtube.api.key}")
  private String apiKey;
  
  // 비디오 제목을 가져오는 메서드
  public String getVideoTitle(String videoUrl) throws IOException {
    String videoId = extractVideoId(videoUrl);
    String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + apiKey;
    
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.getForObject(apiUrl, String.class);
    
    JSONObject jsonResponse = new JSONObject(response);
    JSONArray items = jsonResponse.getJSONArray("items");
    if (items.length() > 0) {
      JSONObject snippet = items.getJSONObject(0).getJSONObject("snippet");
      String title = snippet.getString("title");
      System.out.println("Video Title: " + title);  // 비디오 제목 출력
      return title;
    } else {
      throw new RuntimeException("No video found with the provided URL.");
    }
  }
  
  // 비디오를 오디오 파일로 다운로드하는 메서드
  public void downloadAudio(String videoUrl) throws IOException, InterruptedException {
    String title = getVideoTitle(videoUrl).replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\-_. ]", "_"); // 파일명으로 사용할 수 없는 문자를 _로 변환
    String videoId = extractVideoId(videoUrl);
    
    // yt-dlp 명령어 구성
    String command = "yt-dlp -x --audio-format mp3 -o \"" + title + ".%(ext)s\" https://www.youtube.com/watch?v=" + videoId;
    
    // ProcessBuilder를 사용하여 외부 명령어 실행
    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
    Process process = processBuilder.start();
    
    // 프로세스 출력 스트림 읽기
    try (Scanner scanner = new Scanner(process.getInputStream())) {
      while (scanner.hasNextLine()) {
        System.out.println(scanner.nextLine());
      }
    }
    
    // 프로세스 오류 스트림 읽기
    try (Scanner errorScanner = new Scanner(process.getErrorStream())) {
      while (errorScanner.hasNextLine()) {
        System.err.println(errorScanner.nextLine());
      }
    }
    
    // 프로세스 종료 코드 확인
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new RuntimeException("Failed to download audio. Exit code: " + exitCode);
    }
    
    System.out.println("Audio download completed for video: " + title);  // 다운로드 완료 메시지 출력
  }
  
  // 비디오의 제목을 가져오고 오디오를 다운로드하는 메서드
  public YoutubeResponseDto getVideoDetails(String videoUrl) throws IOException, InterruptedException {
    String title = getVideoTitle(videoUrl);
    downloadAudio(videoUrl);
    System.out.println("Service completed for video: " + title);  // 서비스 완료 메시지 출력
    return new YoutubeResponseDto(title, ""); // 자막은 사용하지 않으므로 빈 문자열 반환
  }
  
  // 비디오 URL에서 비디오 ID를 추출하는 메서드
  private String extractVideoId(String videoUrl) {
    String[] parts = videoUrl.split("v=");
    if (parts.length > 1) {
      String videoId = parts[1];
      int ampersandIndex = videoId.indexOf("&");
      if (ampersandIndex != -1) {
        videoId = videoId.substring(0, ampersandIndex);
      }
      return videoId;
    } else {
      throw new IllegalArgumentException("Invalid video URL");
    }
  }
}