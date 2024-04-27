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

    private String question;

    private String answer;

    private String document;

    private String hashtag1;

    private String hashtag2;

    private String hashtag3;

    @NotBlank
    private String videoUrl;

    @NotBlank
    private String thumbnailUrl;

    @NotBlank
    private String videoTitle;

    private String filtering;

}
