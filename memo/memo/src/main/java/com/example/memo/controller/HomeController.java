package com.example.memo.controller;


import com.example.memo.dto.CategoryDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.repository.CategoryRepository;
import com.example.memo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/home")
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository; // 멤버 정보 저장소 주입

//    @PostMapping("/send-to-home")
//    @CrossOrigin("*")
//    public ResponseEntity<List<CategoryDto>> getCategories(@RequestBody CategoryDto requestDto) {
//        String memberEmail = requestDto.getMemberEmail();
//
//        // 이메일로 멤버 정보를 가져옴
//        MemberEntity member = memberRepository.findByMemberEmail(memberEmail);
//        String memberName = member.getMemberName(); // 멤버 이름 추출
//        System.out.println(memberName);
//        // 해당 멤버의 카테고리 목록을 가져와서 DTO로 변환
//        List<CategoryDto> categoryDtos = categoryRepository
//                .findAllByMemberEmail(memberEmail) // 데이터베이스에서 해당 이메일의 모든 카테고리 가져오기
//                .stream()
//                .map(category -> {
//                    String categoryName = category.getCategoryName();
//                    // categoryName이 null이면 memberName만 포함된 CategoryDto 생성
//                    if (categoryName == null) {
//                        return new CategoryDto(memberName);
//                    } else {
//                        return new CategoryDto(categoryName, memberName);
//                    }
//                })
//                .collect(Collectors.toList());
//
//        // categoryName이 null인 경우에도 memberName이 포함된 DTO를 반환
//        if (categoryDtos.isEmpty()) {
//            categoryDtos.add(new CategoryDto(memberName));
//        }
//
//        return ResponseEntity.ok(categoryDtos); // 성공 응답
//    }
}