package com.example.memo.service;

import com.example.memo.dto.VideoDto;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }
    @Transactional
    public void addVideo(VideoDto videoDto) {
        VideoEntity videoEntity = convertDtoToEntity(videoDto);
        videoRepository.save(videoEntity);
    }
    @Transactional(readOnly = true)
    public List<String> findMostFrequentVideoUrl() {
        PageRequest pageRequest = PageRequest.of(0, 3);  // 첫 페이지에서 상위 3개의 결과만 요청
        List<Object[]> results = videoRepository.findMostFrequentVideoUrl(pageRequest);
        return results.stream().map(result -> (String) result[0]).collect(Collectors.toList());
    }
    private VideoEntity convertDtoToEntity(VideoDto videoDto) {
        //DTO를 Entity로 변환
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setSummary(videoDto.getSummary());
        videoEntity.setDocument(videoDto.getDocument());
        videoEntity.setVideoUrl(videoDto.getVideoUrl());
        videoEntity.setThumbnailUrl(videoDto.getThumbnailUrl());
        videoEntity.setVideoTitle(videoDto.getVideoTitle());
        videoEntity.setFiltering(videoDto.getFiltering());
        return videoEntity;
    }
}
