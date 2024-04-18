package com.example.memo.service.implement;

import com.example.memo.dto.GetSignInUserResponseDto;
import com.example.memo.dto.ResponseDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.repository.MemberRepository;
import com.example.memo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {

    private final MemberRepository memberRepository;

    @Override
    public ResponseEntity<? super GetSignInUserResponseDto> getSignInUser(String memberEmail) {

        MemberEntity memberEntity = null;
        try {
            memberEntity = memberRepository.findByMemberEmail(memberEmail);
            if (memberEntity == null) return GetSignInUserResponseDto.notExistUser();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetSignInUserResponseDto.success(memberEntity);
    }
}
