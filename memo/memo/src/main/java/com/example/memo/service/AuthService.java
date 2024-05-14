package com.example.memo.service;

import com.example.memo.dto.MemberDto;
import com.example.memo.dto.SignInRequestDto;
import com.example.memo.dto.SignInResponseDto;
import com.example.memo.dto.SignUpResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<? super SignUpResponseDto> signUp(MemberDto dto);

    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto);
}
