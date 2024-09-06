package com.example.memo.controller;

import com.example.memo.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
public class PDFController {
  
  @Autowired
  private PDFService pdfService;
  
  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(
          @RequestPart("file") MultipartFile file,
          @RequestPart("memberEmail") String memberEmail,
          @RequestPart("language") String language) {
    
    if (file.isEmpty()) {
      System.out.println("파일이 비었음");
      return ResponseEntity.badRequest().body("Failed to upload because the file is empty");
    }
    
    try {
      // 파일과 이메일을 서비스로 전달
      pdfService.processFile(file, memberEmail, language);
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Failed to process file: " + e.getMessage());
    }
    
    // 성공적으로 처리되었다고 응답
    return ResponseEntity.ok("File received successfully: " + file.getOriginalFilename() + ", Member Email: " + memberEmail);
  }
}
