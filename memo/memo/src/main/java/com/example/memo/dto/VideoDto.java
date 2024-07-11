package com.example.memo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class VideoDto {

    private long videoId;
    //요약본
    private String summary;
    //전체 스크립트
    private String fullScript;
    //비디오 url
    @NotBlank
    private String videoUrl;
    //썸네일
    @NotBlank
    private String thumbnailUrl;
    //비디오 제목
    @NotBlank
    private String videoTitle;
    //카테고리
    private String categoryName;
    //필터
    private String filter;
    //이메일
    @NotBlank
    private String memberEmail;
    //날짜
    private LocalDate documentDate;

    public VideoDto(String videoUrl, String thumbnailUrl, String videoTitle) {
        this.videoUrl=videoUrl;
        this.thumbnailUrl=thumbnailUrl;
        this.videoTitle=videoTitle;
    }
    public VideoDto(String videoUrl, String thumbnailUrl, String videoTitle, String categoryName) {
        this.videoUrl=videoUrl;
        this.thumbnailUrl=thumbnailUrl;
        this.videoTitle=videoTitle;
        this.categoryName=categoryName;
    }
    public VideoDto(String videoTitle, String summary,  String videoUrl, String memberEmail, LocalDate documentDate, String categoryName) {
        this.videoTitle=videoTitle;
        this.summary=summary;
        this.videoUrl=videoUrl;
        this.memberEmail=memberEmail;
        this.documentDate=documentDate;
        this.categoryName=categoryName;
    }
}
