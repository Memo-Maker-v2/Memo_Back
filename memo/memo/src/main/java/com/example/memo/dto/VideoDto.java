package com.example.memo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @NotBlank
    private String recentVideo;

    private String categoryName;

    @NotBlank
    private String memberEmail;

    public VideoDto(String videoTitle, String thumbnailUrl, String videoUrl) {
        this.videoTitle=videoTitle;
        this.thumbnailUrl=thumbnailUrl;
        this.videoUrl=videoUrl;
    }
    public VideoDto(String videoTitle,String summary, String document,String videoUrl,String memberEmail) {
        this.videoTitle=videoTitle;
        this.summary=summary;
        this.document=document;
        this.videoUrl=videoUrl;
        this.memberEmail=memberEmail;
    }
}
