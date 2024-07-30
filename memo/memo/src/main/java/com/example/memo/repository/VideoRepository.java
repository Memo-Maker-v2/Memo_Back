package com.example.memo.repository;

import com.example.memo.entity.VideoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    //조회수 업데이트
    @Modifying
    @Query("UPDATE VideoEntity v SET v.viewCount = v.viewCount + 1 WHERE v.videoId = :videoId")
    void incrementViewCount(long videoId);
}