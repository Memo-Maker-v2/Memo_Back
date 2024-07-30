package com.example.memo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    /*전체 스크립트*/
    @Column
    private String fullScript;

    /*유튜브영상 url*/
    @Column
    private String videoUrl;

    /*썸네일 url*/
    @Column
    private String thumbnailUrl;

    /*유튜브영상 제목*/
    @Column
    private String videoTitle;

    /*사용자가 직접 추가하는 카테고리*/
    @Column(name = "category_name")
    private String categoryName;

    /*필터*/
    @Column
    private String filter;

    @Column(name = "member_email")
    private String memberEmail;

    @Column(name = "document_date")
    private LocalDate documentDate;

    /* 비디오 게시 여부 */
    @Column(name = "is_published")
    private Boolean isPublished = false; // 기본값 false

    @Column(name = "view_count")
    private Long viewCount = 0L; // 조회수 필드, 기본값 0

}
