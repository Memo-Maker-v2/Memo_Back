package com.example.memo.service;

import com.example.memo.dto.user.MemberDto;
import com.example.memo.dto.user.SignInRequestDto;
import com.example.memo.dto.user.SignInResponseDto;
import com.example.memo.dto.user.SignUpResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<? super SignUpResponseDto> signUp(MemberDto dto);

    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto);
}
