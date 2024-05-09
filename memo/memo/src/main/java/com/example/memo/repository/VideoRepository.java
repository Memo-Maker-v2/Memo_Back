package com.example.memo.repository;

import com.example.memo.entity.VideoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    //가장 많이 저장된 video 3개 찾는 query
    @Query("SELECT v.videoTitle, v.thumbnailUrl, v.videoUrl, COUNT(v.videoUrl) AS count FROM VideoEntity v GROUP BY v.videoTitle, v.thumbnailUrl, v.videoUrl ORDER BY count DESC")
    List<Object[]> findMostFrequentVideos(Pageable pageable);

    //필기내용 검색 query
    @Query("SELECT v.videoId FROM VideoEntity v WHERE LOWER(v.document) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Long> findVideoIdsByDocumentContaining(@Param("keyword") String keyword);

    VideoEntity findByMemberEmailAndVideoUrl(String memberEmail, String videoUrl);
    VideoEntity findByVideoUrl(String videoUrl);
}