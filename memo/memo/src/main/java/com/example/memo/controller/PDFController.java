package com.example.memo.controller;

import com.example.memo.dto.PDFResponseDTO;
import com.example.memo.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
      // PDF 파일 처리 및 메타데이터 반환
      PDFResponseDTO pdfResponseDTO = pdfService.processFile(file, memberEmail, language);
      
      // 파일 경로 가져오기
      File pdfFile = pdfService.getPDFFile(memberEmail, pdfResponseDTO.getPdfTitle());
      
      // 파일이 존재하지 않으면 404 반환
      if (!pdfFile.exists()) {
        return ResponseEntity.status(404).body("PDF file not found");
      }
      
      // PDF 파일을 FileSystemResource로 변환
      FileSystemResource fileResource = new FileSystemResource(pdfFile);
      
      // PDF 메타데이터와 파일을 함께 멀티파트로 반환
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("pdfInfo", pdfResponseDTO); // JSON 형태의 메타데이터
      body.add("pdfFile", fileResource); // 파일
      
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA); // 멀티파트 설정
      
      return new ResponseEntity<>(body, headers, HttpStatus.OK);
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
