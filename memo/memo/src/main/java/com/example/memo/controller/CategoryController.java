package com.example.memo.controller;

import com.example.memo.dto.CategoryDto;
import com.example.memo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){this.categoryService=categoryService;}
    //category 저장
    @PostMapping
    public ResponseEntity<Void> addCategory(@RequestBody CategoryDto categoryDto) {
        categoryService.addCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //category 삭제
    @DeleteMapping("delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") long categoryId) {
        if (categoryService.deleteCategory(categoryId)) {
            return ResponseEntity.ok().build(); // 성공적으로 삭제되면 200 OK 응답
        } else {
            return ResponseEntity.notFound().build(); // 해당 ID의 카테고리가 없는 경우 404 Not Found 응답
        }
    }
}
