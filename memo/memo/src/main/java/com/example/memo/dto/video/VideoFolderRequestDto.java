package com.example.memo.dto.video;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class VideoFolderRequestDto {
    private String memberEmail;
    private String videoUrl;
    private String categoryName;
}
