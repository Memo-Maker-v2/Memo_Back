package com.example.memo.controller;

import com.example.memo.dto.PDFResponseDTO;
import com.example.memo.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
public class PDFController {
  
  @Autowired
  private PDFService pdfService;
  
  @PostMapping("/pdfupload")
  public ResponseEntity<?> uploadFile(
          @RequestPart("file") MultipartFile file,
          @RequestPart("memberEmail") String memberEmail,
          @RequestPart("language") String language) {
    
    if (file.isEmpty()) {
      System.out.println("파일이 비었음");
      return ResponseEntity.badRequest().body("Failed to upload because the file is empty");
    }
    
    try {
      // 파일과 이메일을 서비스로 전달하여 요약 및 다른 PDF 정보 받기
      PDFResponseDTO pdfResponseDTO = pdfService.processFile(file, memberEmail, language);
      
      // PDFResponseDTO를 프론트엔드에 반환
      return ResponseEntity.ok(pdfResponseDTO);
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Failed to process file: " + e.getMessage());
    }
  }
  
  @PostMapping("/getpdffile")
  public ResponseEntity<?> getPDFByEmailAndTitle(
          @RequestPart("memberEmail") String memberEmail,
          @RequestPart("pdfTitle") String pdfTitle) {
    
    try {
      // PDF 메타데이터 및 파일을 서비스로부터 조회
      PDFResponseDTO pdfResponseDTO = pdfService.getPDFByEmailAndTitle(memberEmail, pdfTitle);
      
      // 파일 경로 가져오기
      File pdfFile = pdfService.getPDFFile(memberEmail, pdfTitle);
      
      // 파일이 존재하지 않으면 404 반환
      if (!pdfFile.exists()) {
        return ResponseEntity.status(404).body("PDF file not found");
      }
      
      // 파일과 메타데이터를 반환 (Multipart 반환)
      return ResponseEntity.ok()
              .header("Content-Disposition", "attachment; filename=\"" + pdfFile.getName() + "\"")
              .header("Content-Type", "application/pdf")
              .body(new org.springframework.core.io.FileSystemResource(pdfFile));
      
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Error while fetching PDF: " + e.getMessage());
    }
  }
  
}
