package com.example.memo.repository;

import com.example.memo.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>{
    List<QuestionEntity> findByVideoUrlAndMemberEmail(String videoUrl, String memberEmail);

}
