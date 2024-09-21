package com.example.memo.controller;

import com.example.memo.dto.PDFRequestDTO;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
  public ResponseEntity<FileSystemResource> getPDFByEmailAndTitle(
          @RequestBody PDFRequestDTO requestDTO) {
    try {
      String memberEmail = requestDTO.getMemberEmail();
      String pdfTitle = requestDTO.getPdfTitle();
      
      File pdfFile = pdfService.getPDFFile(memberEmail, pdfTitle);
      
      if (!pdfFile.exists()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      
      FileSystemResource fileResource = new FileSystemResource(pdfFile);
      String encodedFileName = URLEncoder.encode(pdfFile.getName(), StandardCharsets.UTF_8.toString())
              .replaceAll("\\+", "%20");
      
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", encodedFileName);
      return new ResponseEntity<>(fileResource, headers, HttpStatus.OK);
      
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
  @PostMapping("/getpdfinfo")
  public ResponseEntity<PDFResponseDTO> getPDFInfo(
          @RequestBody PDFRequestDTO requestDTO) {
    try {
      String memberEmail = requestDTO.getMemberEmail();
      String pdfTitle = requestDTO.getPdfTitle();
      
      // PDF 정보 조회
      PDFResponseDTO pdfResponseDTO = pdfService.getPDFByEmailAndTitle(memberEmail, pdfTitle);
      
      if (pdfResponseDTO == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      
      return ResponseEntity.ok(pdfResponseDTO);
      
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
