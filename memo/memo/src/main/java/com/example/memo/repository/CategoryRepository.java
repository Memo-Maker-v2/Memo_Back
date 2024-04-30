package com.example.memo.repository;

import com.example.memo.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
//    List<CategoryEntity> findByParentId(Long parentId);//부모 Id로 카테고리 검색
}
