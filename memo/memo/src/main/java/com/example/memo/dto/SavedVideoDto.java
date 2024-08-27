package com.example.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedVideoDto {

    private long id;
    private long videoId;
    private String memberEmail;
    private LocalDate savedDate;
    private String videoTitle; // 추가된 필드
    private String memberName; // 추가된 필드

    public SavedVideoDto(long id, long videoId, String memberEmail, LocalDate savedDate) {
        this.id=id;
        this.videoId=videoId;
        this.memberEmail=memberEmail;
        this.savedDate=savedDate;
    }
}
