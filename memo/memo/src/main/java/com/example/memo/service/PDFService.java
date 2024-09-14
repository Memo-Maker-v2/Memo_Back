package com.example.memo.service;

import com.example.memo.common.OpenAIUtils;
import com.example.memo.dto.PDFDTO;
import com.example.memo.dto.PDFResponseDTO;
import com.example.memo.repository.PDFRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

@Service
public class PDFService {
  
  // PDF 폴더 경로 설정 (프로젝트 내의 resources 디렉토리 하위에 pdfs 폴더를 생성)
  private static final String PDF_FOLDER = "memo/src/main/resources/pdfs";
  
  @Autowired
  private OpenAIUtils openAIUtils;
  
  @Autowired
  private PDFRepository pdfRepository;
  
  public PDFResponseDTO processFile(MultipartFile file, String memberEmail, String language) throws IOException {
    // 파일이 비어 있는지 확인
    if (file.isEmpty()) {
      throw new IOException("File is empty");
    }
    
    // 현재 작업 디렉토리 출력
    String currentDir = System.getProperty("user.dir");
    System.out.println("Current working directory: " + currentDir);
    
    // memberEmail을 폴더 이름으로 사용하여 계정별 폴더 생성
    String sanitizedEmail = memberEmail.replaceAll("[^a-zA-Z0-9]", "_"); // 이메일을 안전하게 변환
    File accountDirectory = new File(currentDir + "/" + PDF_FOLDER + "/" + sanitizedEmail);
    if (!accountDirectory.exists()) {
      boolean dirCreated = accountDirectory.mkdirs(); // 계정별 폴더 생성
      if (!dirCreated) {
        throw new IOException("Failed to create directory for account: " + sanitizedEmail);
      }
    }
    
    // 파일 저장 경로 설정 (계정별 폴더 내에 파일 저장)
    Path filePath = Paths.get(accountDirectory.getAbsolutePath(), file.getOriginalFilename());
    
    // 파일 저장
    file.transferTo(filePath.toFile());
    
    // PDF 제목을 파일 이름으로 설정
    String pdfTitle = file.getOriginalFilename();
    
    // 파일 정보 콘솔에 출력
    System.out.println("Received file: " + file.getOriginalFilename());
    System.out.println("File size: " + file.getSize() + " bytes");
    System.out.println("Received member email: " + memberEmail);
    System.out.println("Received language: " + language);
    System.out.println("File saved to: " + filePath.toString());
    
    // 1. 이메일과 PDF 제목을 기준으로 중복 체크
    Optional<PDFDTO> existingPDF = pdfRepository.findByMemberEmailAndPdfTitle(memberEmail, pdfTitle);
    if (existingPDF.isPresent()) {
      PDFDTO pdfDTO = existingPDF.get();
      System.out.println("이미 저장된 PDF입니다: " + pdfTitle);
      // 중복된 데이터가 있으면 해당 PDF 정보를 DTO로 반환
      return new PDFResponseDTO(
              pdfDTO.getPdfId(),
              pdfDTO.getSummary(),
              pdfDTO.getFullScript(),
              pdfDTO.getPdfTitle(),
              pdfDTO.getMemberEmail(),
              pdfDTO.getDocumentDate(),
              pdfDTO.getCategoryName(),
              pdfDTO.getFilter(),
              pdfDTO.getIsPublished(),
              pdfDTO.getViewCount()
      );
    }
    // 중복되지 않은 경우 PDF 파일에서 텍스트 추출
    String text = extractTextFromPDF(filePath.toFile());
    System.out.println("PDF의 내용을 추출 했습니다.\n" + text.length() + "글자");
    // OpenAI를 사용하여 텍스트 요약
    String summary = openAIUtils.summarize(text, language, "pdf");
    
    // 요약 결과 출력
    System.out.println("PDFSummary: ");
    System.out.println(summary);
    
    // 현재 시간으로 document_date 설정
    Date documentDate = new Date();
    
    // DB에 저장할 데이터 구성
    PDFDTO pdfDTO = new PDFDTO();
    pdfDTO.setSummary(summary);
    pdfDTO.setFullScript(text);
    pdfDTO.setPdfTitle(pdfTitle);
    pdfDTO.setMemberEmail(memberEmail);
    pdfDTO.setDocumentDate(documentDate);
    
    // DB에 저장
    pdfRepository.save(pdfDTO);
    
    // PDFResponseDTO로 반환
    // 모든 필드를 포함한 PDFResponseDTO로 반환
    return new PDFResponseDTO(
            pdfDTO.getPdfId(),
            pdfDTO.getSummary(),
            pdfDTO.getFullScript(),
            pdfDTO.getPdfTitle(),
            pdfDTO.getMemberEmail(),
            pdfDTO.getDocumentDate(),
            pdfDTO.getCategoryName(),
            pdfDTO.getFilter(),
            pdfDTO.getIsPublished(),
            pdfDTO.getViewCount()
    );
  }
  
  // PDF에서 텍스트를 추출하는 메소드
  private String extractTextFromPDF(File file) throws IOException {
    try (PDDocument document = PDDocument.load(file)) {
      PDFTextStripper stripper = new PDFTextStripper();
      return stripper.getText(document);
    }
  }
  
  
  public PDFResponseDTO getPDFByEmailAndTitle(String memberEmail, String pdfTitle) throws IOException {
    // 이메일과 PDF 제목을 기준으로 DB에서 PDF 정보 조회
    Optional<PDFDTO> optionalPDF = pdfRepository.findByMemberEmailAndPdfTitle(memberEmail, pdfTitle);
    
    if (optionalPDF.isEmpty()) {
      throw new IOException("PDF not found in the database");
    }
    
    PDFDTO pdfDTO = optionalPDF.get();
    
    // PDFResponseDTO로 반환
    return new PDFResponseDTO(
            pdfDTO.getPdfId(),
            pdfDTO.getSummary(),
            pdfDTO.getFullScript(),
            pdfDTO.getPdfTitle(),
            pdfDTO.getMemberEmail(),
            pdfDTO.getDocumentDate(),
            pdfDTO.getCategoryName(),
            pdfDTO.getFilter(),
            pdfDTO.getIsPublished(),
            pdfDTO.getViewCount()
    );
  }
  
  public File getPDFFile(String memberEmail, String pdfTitle) {
    // 파일 경로에서 memberEmail을 폴더명으로 사용
    String sanitizedEmail = memberEmail.replaceAll("[^a-zA-Z0-9]", "_");
    String filePath = System.getProperty("user.dir") + "/" + PDF_FOLDER + "/" + sanitizedEmail + "/" + pdfTitle;
    
    // PDF 파일 반환
    return new File(filePath);
  }
  
  
}
