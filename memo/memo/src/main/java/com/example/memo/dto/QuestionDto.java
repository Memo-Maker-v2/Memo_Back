package com.example.memo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuestionDto {

    private long questionId;

    private String question;

    private String answer;

    private String memberEmail;

    private String videoUrl;
    public QuestionDto(String question, String answer,String memberEmail,String videoUrl) {
        this.question = question;
        this.answer = answer;
        this.memberEmail=memberEmail;
        this.videoUrl=videoUrl;
    }
}
