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
@Table(name = "video_table")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long videoId;

    /*gpt 동영상요약내용*/
    @Column
    private String summary;

    /*필기내용*/
    @Column
    private String document;

    @Column
    private String hashtag1;

    @Column
    private String hashtag2;

    @Column
    private String hashtag3;

    /*유튜브영상 url*/
    @Column
    private String videoUrl;

    /*썸네일 url*/
    @Column
    private String thumbnailUrl;

    /*유튜브영상 제목*/
    @Column
    private String videoTitle;

    /*영상주제 필터링 카테고리*/
    @Column
    private String filtering;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;
}
