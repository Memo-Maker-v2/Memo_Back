package com.example.memo.dto.video;

import com.example.memo.entity.VideoEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class VideoDto {
    
    private long videoId;
    private String summary; // 요약본
    private String fullScript; // 전체 스크립트
    @NotBlank
    private String videoUrl; // 비디오 URL
    @NotBlank
    private String thumbnailUrl; // 썸네일
    @NotBlank
    private String videoTitle; // 비디오 제목
    private String categoryName; // 카테고리
    private String filter; // 필터
    @NotBlank
    private String memberEmail; // 이메일
    private LocalDate documentDate; // 날짜
    private Boolean isPublished; // 게시 여부
    private Long viewCount; // 조회수
    
    // 기존 생성자들 유지
    public VideoDto(String videoUrl, String thumbnailUrl, String videoTitle) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.videoTitle = videoTitle;
    }
    
    public VideoDto(String videoUrl, String thumbnailUrl, String videoTitle, String categoryName) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.videoTitle = videoTitle;
        this.categoryName = categoryName;
    }
    
    public VideoDto(String videoUrl, String thumbnailUrl, String videoTitle, String filter, LocalDate documentDate) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.videoTitle = videoTitle;
        this.filter = filter;
        this.documentDate = documentDate;
    }
    
    public VideoDto(String videoTitle, String summary, String fullScript, String videoUrl, String memberEmail, LocalDate documentDate, String categoryName) {
        this.videoTitle = videoTitle;
        this.summary = summary;
        this.fullScript = fullScript;
        this.videoUrl = videoUrl;
        this.memberEmail = memberEmail;
        this.documentDate = documentDate;
        this.categoryName = categoryName;
    }
    
    // 추가된 생성자
    public VideoDto(String videoUrl, String thumbnailUrl, String videoTitle, String categoryName, String filter, LocalDate documentDate, Boolean isPublished, Long viewCount) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.videoTitle = videoTitle;
        this.categoryName = categoryName;
        this.filter = filter;
        this.documentDate = documentDate;
        this.isPublished = isPublished;
        this.viewCount = viewCount;
    }

    // 비어 있는 생성자 완성
    public VideoDto(String videoTitle, String summary, String fullScript, String videoUrl, String memberEmail, LocalDate documentDate, String categoryName, String filter, Boolean isPublished, Long viewCount) {
        this.videoTitle = videoTitle;
        this.summary = summary;
        this.fullScript = fullScript;
        this.videoUrl = videoUrl;
        this.memberEmail = memberEmail;
        this.documentDate = documentDate;
        this.categoryName = categoryName;
        this.filter = filter;
        this.isPublished = isPublished;
        this.viewCount = viewCount;
    }
    
    // VideoEntity를 받아서 VideoDto로 변환하는 생성자
    public VideoDto(VideoEntity videoEntity) {
        this.videoId = videoEntity.getVideoId();
        this.summary = videoEntity.getSummary();
        this.fullScript = videoEntity.getFullScript();
        this.videoUrl = videoEntity.getVideoUrl();
        this.thumbnailUrl = videoEntity.getThumbnailUrl();
        this.videoTitle = videoEntity.getVideoTitle();
        this.categoryName = videoEntity.getCategoryName();
        this.filter = videoEntity.getFilter();
        this.memberEmail = videoEntity.getMemberEmail();
        this.documentDate = videoEntity.getDocumentDate();
        this.isPublished = videoEntity.getIsPublished();
        this.viewCount = videoEntity.getViewCount();
    }
}
