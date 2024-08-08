package com.example.memo.repository;

import com.example.memo.entity.VideoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeVideoRepository extends JpaRepository<VideoEntity, Long> {
  boolean existsByVideoUrlAndMemberEmail(String videoUrl, String memberEmail);
  
  @Query("SELECT v.videoUrl, v.thumbnailUrl, v.videoTitle, COUNT(v.videoUrl) AS count " +
          "FROM VideoEntity v GROUP BY v.videoTitle, v.thumbnailUrl, v.videoUrl " +
          "ORDER BY count DESC")
  List<Object[]> findMostFrequentVideos(Pageable pageable);
  
  VideoEntity findByMemberEmailAndVideoUrl(String memberEmail, String videoUrl);
  
  VideoEntity findByVideoUrl(String videoUrl);
  
  List<VideoEntity> findByCategoryNameAndMemberEmail(String categoryName, String memberEmail);
  
  List<VideoEntity> findByMemberEmail(String memberEmail);
  
  List<VideoEntity> findByMemberEmailAndCategoryName(String memberEmail, String categoryName);
}
