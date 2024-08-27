package com.example.memo.dto.user;

import com.example.memo.common.ResponseCode;
import com.example.memo.common.ResponseMessage;
import com.example.memo.dto.ResponseDto;
import com.example.memo.entity.MemberEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetSignInUserResponseDto extends ResponseDto {

    private String memberEmail;
    private String memberName;

    private GetSignInUserResponseDto(MemberEntity memberEntity) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.memberEmail = memberEntity.getMemberEmail();
        this.memberName = memberEntity.getMemberName();
    }

    public static ResponseEntity<GetSignInUserResponseDto> success(MemberEntity memberEntity) {
        GetSignInUserResponseDto result = new GetSignInUserResponseDto(memberEntity);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistUser() {
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_USER, ResponseMessage.NOT_EXISTED_USER);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
}
