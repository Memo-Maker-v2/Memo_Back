package com.example.memo.repository;

import com.example.memo.entity.VideoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    //가장 많이 저장된 video 3개 찾는 query
    @Query("SELECT v.videoUrl, COUNT(v.videoUrl) AS count FROM VideoEntity v GROUP BY v.videoUrl ORDER BY count DESC")
    List<Object[]> findMostFrequentVideoUrl(Pageable pageable);
}