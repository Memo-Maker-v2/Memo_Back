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

    private String summary;

    private String document;

    @NotBlank
    private String videoUrl;

    @NotBlank
    private String thumbnailUrl;

    @NotBlank
    private String videoTitle;

    private String categoryName;

    @NotBlank
    private String memberEmail;

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
    public VideoDto(String videoTitle, String summary, String document, String videoUrl, String memberEmail, LocalDate documentDate) {
        this.videoTitle=videoTitle;
        this.summary=summary;
        this.document=document;
        this.videoUrl=videoUrl;
        this.memberEmail=memberEmail;
        this.documentDate=documentDate;
    }
}
