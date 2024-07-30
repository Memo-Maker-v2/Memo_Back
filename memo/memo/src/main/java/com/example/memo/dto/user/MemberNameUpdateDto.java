package com.example.memo.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberNameUpdateDto {
    private String memberEmail;
    private String newName;

}
