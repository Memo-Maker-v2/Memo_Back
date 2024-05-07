package com.example.memo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategoryDto {

    private long categoryId;

    @NotBlank
    private String categoryName;

    private long categorySequence;

    private String memberEmail;
}
