package com.example.memo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategoryUpdateDto {
    private String memberEmail;
    private String oldCategoryName;
    private String newCategoryName;
}

