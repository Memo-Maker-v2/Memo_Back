package com.example.memo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategoryDto {

    private long categoryId;

    @NotBlank
    private String categoryName;

    @NotBlank
    private String memberEmail;

    @NotBlank
    private String memberName;  // 추가된 필드

    // 모든 필드를 포함하는 생성자
    public CategoryDto(String categoryName, String memberName) { // 생성자 수정
        this.categoryName = categoryName;
        this.memberName = memberName;
    }

    // 멤버 이름만 포함하는 생성자
    public CategoryDto(String memberName) {
        this.memberName = memberName;
    }
}
