package com.example.memo.dto.user;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignInRequestDto {
    @NotBlank
    private String memberEmail;
    @NotBlank
    private String memberPassword;
}
