package com.example.memo.controller;

import com.example.memo.dto.SavedVideoDto;
import com.example.memo.dto.video.VideoDto;
import com.example.memo.service.SavedVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/video")
public class SavedVideoController {
    @Autowired
    private SavedVideoService savedVideoService;

    @PostMapping("/saved-videos")
    public List<SavedVideoDto> getSavedVideos(@RequestBody Map<String, String> request) {
        String memberEmail = request.get("memberEmail");
        return savedVideoService.getAllSavedVideos(memberEmail);
    }
    @PostMapping("/get-video")
    public Optional<VideoDto> getVideoById(@RequestBody VideoIdRequest videoIdRequest) {
        return savedVideoService.getVideoById(videoIdRequest.getVideoId());
    }

    public static class VideoIdRequest {
        private long videoId;

        public long getVideoId() {
            return videoId;
        }

        public void setVideoId(long videoId) {
            this.videoId = videoId;
        }
    }
}
