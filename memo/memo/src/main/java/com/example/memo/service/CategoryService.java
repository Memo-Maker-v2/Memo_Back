package com.example.memo.service;

import com.example.memo.dto.CategoryDto;
import com.example.memo.entity.CategoryEntity;
import com.example.memo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Transactional
    public void addCategory(CategoryDto categoryDto) {
        CategoryEntity categoryEntity = convertDtoToEntity(categoryDto);
        categoryRepository.save(categoryEntity);
    }
    private CategoryEntity convertDtoToEntity(CategoryDto categoryDto) {
        //DTO를 Entity로 변환
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategoryName(categoryDto.getCategoryName());
        categoryEntity.setCategorySequence(categoryDto.getCategorySequence());
        return categoryEntity;
    }
}
