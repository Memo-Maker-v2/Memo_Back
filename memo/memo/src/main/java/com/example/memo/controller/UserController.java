package com.example.memo.controller;


import com.example.memo.dto.GetSignInUserResponseDto;
import com.example.memo.dto.MemberNameUpdateDto;
import com.example.memo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<? super GetSignInUserResponseDto> getSignInUser(@AuthenticationPrincipal String memberEmail) {
       ResponseEntity<? super GetSignInUserResponseDto> response = userService.getSignInUser(memberEmail);
       return response;
    }
    //이름변경
    @PatchMapping("/update-name")
    @CrossOrigin("*")
    public ResponseEntity<?> updateMemberName(@RequestBody MemberNameUpdateDto memberNameUpdateDto) {
        return userService.updateMemberName(memberNameUpdateDto.getMemberEmail(), memberNameUpdateDto.getNewName());
    }
}
