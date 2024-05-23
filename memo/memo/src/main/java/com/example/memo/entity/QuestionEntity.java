package com.example.memo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question_table")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private long questionId;
    /*gpt 질문내용*/
    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

    @Column(name = "member_email")
    private String memberEmail;

    @Column(name = "video_url")
    private String videoUrl;
}
