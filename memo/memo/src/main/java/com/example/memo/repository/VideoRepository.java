package com.example.memo.repository;

import com.example.memo.dto.video.VideoDto;
import com.example.memo.entity.VideoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    boolean existsByVideoUrlAndMemberEmail(String videoUrl, String memberEmail);
    
    //가장 많이 저장된 video 3개 찾는 query
    @Query("SELECT v.videoUrl, v.thumbnailUrl, v.videoTitle, COUNT(v.videoUrl) AS count FROM VideoEntity v GROUP BY v.videoTitle, v.thumbnailUrl, v.videoUrl ORDER BY count DESC")
    List<Object[]> findMostFrequentVideos(Pageable pageable);

    VideoEntity findByMemberEmailAndVideoUrl(String memberEmail, String videoUrl);
    VideoEntity findByVideoUrl(String videoUrl);
    //category별 video 검색
    List<VideoEntity> findByCategoryNameAndMemberEmail(String categoryName, String memberEmail);
    List<VideoEntity> findByMemberEmail(String memberEmail);
    //category삭제
    List<VideoEntity> findByMemberEmailAndCategoryName(String memberEmail, String categoryName);
    //필터별 영상 조회
    List<VideoEntity> findByFilter(String filter);

    // 필터와 isPublished가 true인 비디오를 찾는 메서드
    List<VideoEntity> findByFilterAndIsPublishedTrue(String filter);
    //video의 is_published가 true인 값 찾음
    List<VideoEntity> findByIsPublishedTrue();

    List<VideoEntity> findByIsPublishedTrueOrderByViewCountDesc();
    @Modifying
    @Transactional
    @Query("UPDATE VideoEntity v SET v.viewCount = v.viewCount + 1 WHERE v.memberEmail = :memberEmail AND v.videoUrl = :videoUrl")
    void incrementViewCount(@Param("memberEmail") String memberEmail, @Param("videoUrl") String videoUrl);
    List<VideoEntity> findByIsPublishedTrueOrderByDocumentDateDesc();
    // 제목으로 검색하고 게시된 비디오만 반환
    List<VideoEntity> findByVideoTitleContainingAndIsPublishedTrue(String title);
}