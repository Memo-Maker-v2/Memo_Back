package com.example.memo.dto;

import com.example.memo.entity.MemberEntity;
import lombok.*;

//DTO:데이터 전송 객체(Data Transfer Object), 프로젝트 간에 데이터를 전달하는 객체(단순히 데이터 전달 목적)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberDTO {//회원 정보를 필드로 정의
    private Long Uid;
    private String memberEmail;
    private String memberPassword;
    private String memberName;

    public static MemberDTO toMemberDTO(MemberEntity memberEntity){
        MemberDTO memberDTO=new MemberDTO();
        memberDTO.setUid(memberEntity.getUid());
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());

        return memberDTO;
    }
}
