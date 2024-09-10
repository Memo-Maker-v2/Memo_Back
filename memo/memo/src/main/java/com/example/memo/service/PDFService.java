package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PDFService {
  
  // PDF 폴더 경로 설정 (프로젝트 내의 resources 디렉토리 하위에 pdfs 폴더를 생성)
  private static final String PDF_FOLDER = "memo/src/main/resources/pdfs";
  
  @Autowired
  private OpenAIUtils openAIUtils;
  
  public String processFile(MultipartFile file, String memberEmail, String language) throws IOException {
    // 파일이 비어 있는지 확인
    if (file.isEmpty()) {
      throw new IOException("File is empty");
    }
    
    // 현재 작업 디렉토리 출력
    String currentDir = System.getProperty("user.dir");
    System.out.println("Current working directory: " + currentDir);
    
    // pdfs 폴더가 있는지 확인하고 없으면 생성
    File directory = new File(currentDir + "/" + PDF_FOLDER);
    if (!directory.exists()) {
      boolean dirCreated = directory.mkdirs(); // 디렉토리 생성
      if (!dirCreated) {
        throw new IOException("Failed to create directory: " + PDF_FOLDER);
      }
    }
    
    // 파일 저장 경로 설정
    Path filePath = Paths.get(currentDir + "/" + PDF_FOLDER, file.getOriginalFilename());
    
    // 파일 저장
    file.transferTo(filePath.toFile());
    
    // 파일 정보 콘솔에 출력
    System.out.println("Received file: " + file.getOriginalFilename());
    System.out.println("File size: " + file.getSize() + " bytes");
    System.out.println("Received member email: " + memberEmail);
    System.out.println("Received language: " + language);
    System.out.println("File saved to: " + filePath.toString());
    
    // PDF 파일에서 텍스트 추출
    String text = extractTextFromPDF(filePath.toFile());
    
    System.out.println("text = " + text);
    
    // OpenAI를 사용하여 텍스트 요약
    String summary = openAIUtils.summarize(text, language, "pdf");
    
    // 요약 결과 출력
    System.out.println("PDFSummary: ");
    System.out.println(summary);
    
    // 요약 결과 반환
    return summary;
  }
  
  // PDF에서 텍스트를 추출하는 메소드
  private String extractTextFromPDF(File file) throws IOException {
    try (PDDocument document = PDDocument.load(file)) {
      PDFTextStripper stripper = new PDFTextStripper();
      return stripper.getText(document);
    }
  }
}
