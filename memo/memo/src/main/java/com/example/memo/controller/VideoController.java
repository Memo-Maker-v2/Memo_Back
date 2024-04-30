package com.example.memo.controller;

import com.example.memo.dto.VideoDto;
import com.example.memo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }
    //가장 많이 본 video 3개
    @GetMapping("/most-frequent-url")
    public ResponseEntity<List<String>> getMostFrequentVideoUrls() {
        List<String> urls = videoService.findMostFrequentVideoUrl();
        if (urls != null && !urls.isEmpty()) {
            return ResponseEntity.ok(urls);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //video 정보 추가
    @PostMapping
    public ResponseEntity<Void> addVideo(@RequestBody VideoDto videoDto) {
        videoService.addVideo(videoDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
