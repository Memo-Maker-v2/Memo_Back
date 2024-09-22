package com.example.memo.repository;

import com.example.memo.dto.PDFDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PDFRepository extends JpaRepository<PDFDTO, Long> {
  
  List<PDFDTO> findByMemberEmail(String memberEmail);
  
  // 이메일과 PDF 제목으로 데이터 조회
  Optional<PDFDTO> findByMemberEmailAndPdfTitle(String memberEmail, String pdfTitle);
}