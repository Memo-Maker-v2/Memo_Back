package com.example.memo.service;

import com.example.memo.dto.MemberDTO;
import com.example.memo.dto.SignInRequestDto;
import com.example.memo.dto.SignInResponseDto;
import com.example.memo.dto.SignUpResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<? super SignUpResponseDto> signUp(MemberDTO dto);
    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto);
}
