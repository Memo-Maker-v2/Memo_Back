package com.example.memo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@ToString
public class VideoAndQuestionDto {
    private VideoDto video;
    private List<QuestionDto> questions;
    public VideoAndQuestionDto(VideoDto video, List<QuestionDto> questions) {
        this.video = video;
        this.questions = questions;
    }
}
