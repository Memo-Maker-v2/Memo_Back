package com.example.memo.repository;

import com.example.memo.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>{
    
    // 중복된 질문이 있는지 확인하기 위한 메서드
    boolean existsByMemberEmailAndVideoUrl(String memberEmail, String videoUrl);
    
    List<QuestionEntity> findByMemberEmailAndVideoUrl(String memberEmail, String videoUrl);

}
