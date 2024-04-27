package com.example.memo.service;

import com.example.memo.dto.VideoDto;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private VideoEntity convertDtoToEntity(VideoDto videoDto) {
        //DTO를 Entity로 변환
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setSummary(videoDto.getSummary());
        videoEntity.setQuestion(videoDto.getQuestion());
        videoEntity.setDocument(videoDto.getDocument());
        videoEntity.setHashtag1(videoDto.getHashtag1());
        videoEntity.setHashtag2(videoDto.getHashtag2());
        videoEntity.setHashtag3(videoDto.getHashtag3());
        videoEntity.setVideoUrl(videoDto.getVideoUrl());
        videoEntity.setThumbnailUrl(videoDto.getThumbnailUrl());
        videoEntity.setVideoTitle(videoDto.getVideoTitle());
        videoEntity.setFiltering(videoDto.getFiltering());
        return videoEntity;
    }
}
