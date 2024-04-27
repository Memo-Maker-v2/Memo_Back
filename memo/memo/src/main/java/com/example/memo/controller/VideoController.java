package com.example.memo.controller;

import com.example.memo.dto.VideoDto;
import com.example.memo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<Void> addVideo(@RequestBody VideoDto videoDto) {
        videoService.addVideo(videoDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
