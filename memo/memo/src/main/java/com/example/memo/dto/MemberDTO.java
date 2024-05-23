package com.example.memo.dto;

import com.example.memo.entity.MemberEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

//DTO:데이터 전송 객체(Data Transfer Object), 프로젝트 간에 데이터를 전달하는 객체(단순히 데이터 전달 목적)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberDto {//회원 정보를 필드로 정의
    
    private Long Uid;
    @NotBlank
    private String memberEmail;
    @NotBlank
    private String memberPassword;
    @NotBlank
    private String memberName;

    public static MemberDto toMemberDTO(MemberEntity memberEntity){
        MemberDto memberDTO=new MemberDto();
        memberDTO.setUid(memberEntity.getUid());
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());

        return memberDTO;
    }
}

