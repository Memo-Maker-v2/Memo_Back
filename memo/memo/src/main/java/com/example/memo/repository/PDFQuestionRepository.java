package com.example.memo.repository;

import com.example.memo.entity.PDFQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PDFQuestionRepository extends JpaRepository<PDFQuestionEntity, Long> {
  // 추가적인 쿼리 메소드가 필요하면 여기에 작성
}
