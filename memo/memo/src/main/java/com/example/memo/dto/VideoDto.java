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

    private String filtering;

}
