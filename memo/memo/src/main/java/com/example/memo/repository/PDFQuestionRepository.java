package com.example.memo.repository;

import com.example.memo.entity.PDFQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PDFQuestionRepository extends JpaRepository<PDFQuestionEntity, Long> {
  // 추가적인 쿼리 메소드가 필요하면 여기에 작성
  
  // memberEmail과 pdfTitle로 PDF 질문 리스트를 조회
  List<PDFQuestionEntity> findByMemberEmailAndPdfTitle(String memberEmail, String pdfTitle);
  
}
