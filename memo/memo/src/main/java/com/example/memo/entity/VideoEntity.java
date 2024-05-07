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

    /*유튜브영상 url*/
    @Column
    private String videoUrl;

    /*썸네일 url*/
    @Column
    private String thumbnailUrl;

    /*유튜브영상 제목*/
    @Column
    private String videoTitle;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "member_email")
    private String memberEmail;
}
