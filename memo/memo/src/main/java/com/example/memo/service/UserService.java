package com.example.memo.service;

import com.example.memo.dto.user.GetSignInUserResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<? super GetSignInUserResponseDto> getSignInUser(String memberEmail);
    ResponseEntity<?> updateMemberName(String email, String newName);
}
