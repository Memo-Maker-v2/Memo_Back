package com.example.memo.controller;


import com.example.memo.dto.CategoryDto;
import com.example.memo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryRepository categoryRepository;

    @PostMapping("/send-to-home")
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestBody CategoryDto requestDto) {
        String memberEmail = requestDto.getMemberEmail();

        List<CategoryDto> categoryDtos = categoryRepository
                .findAllByMemberEmail(memberEmail) // 데이터베이스에서 해당 이메일의 모든 카테고리 가져오기
                .stream()
                .map(category -> new CategoryDto(category.getCategoryName())) // DTO로 변환
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryDtos); // 성공 응답
    }
}
