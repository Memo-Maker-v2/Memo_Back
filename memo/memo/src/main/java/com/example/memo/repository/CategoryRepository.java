package com.example.memo.repository;

import com.example.memo.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    // 회원 이메일로 모든 카테고리 찾기
    List<CategoryEntity> findAllByMemberEmail(String memberEmail);

    CategoryEntity findByMemberEmailAndCategoryName(String memberEmail, String oldCategoryName);
}
