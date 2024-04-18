package com.example.memo.entity;

import com.example.memo.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_table")//database에 해당 이름의 테이블 생성
public class MemberEntity { //table 역할
    //jpa => databae를 객체처럼 사용 가능
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @Column(unique = true)
    private String memberEmail;

    @Column
    private String memberPassword;

    @Column
    private String memberName;

    public MemberEntity(MemberDTO dto) {
        this.uid = dto.getUid();
        this.memberEmail = dto.getMemberEmail();
        this.memberPassword = dto.getMemberPassword();
        this.memberName = dto.getMemberName();
    }


    public static MemberEntity toMemberEntity(MemberDTO memberDTO){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUid(memberDTO.getUid());
        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        return memberEntity;
    }
}


