package com.example.memo.controller;

import com.example.memo.dto.video.VideoDto;
import com.example.memo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/community")
public class CommunityController {
    private final VideoService videoService;
    @Autowired
    public CommunityController(VideoService videoService) {
        this.videoService = videoService;
    }
    //게시된 비디오만 불러오기
    @GetMapping("/published-videos")
    @CrossOrigin("*")
    public ResponseEntity<List<VideoDto>> getPublishedVideos() {
        List<VideoDto> videos = videoService.findPublishedVideos();
        return ResponseEntity.ok(videos);
    }
    //게시물 클릭했을때 video 정보
    @PostMapping("/video")
    @CrossOrigin("*")
    public ResponseEntity<VideoDto> getVideoInfo(@RequestBody Map<String, String> requestBody) {
        String memberEmail = requestBody.get("memberEmail");
        String videoUrl = requestBody.get("videoUrl");

        if (memberEmail == null || memberEmail.isEmpty() || videoUrl == null || videoUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        VideoDto video = videoService.findVideoByEmailAndUrl(memberEmail, videoUrl);
        if (video != null) {
            return ResponseEntity.ok(video);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //조회수 순으로 정렬(인기순)
    @GetMapping("/popular")
    @CrossOrigin("*")
    public ResponseEntity<List<VideoDto>> getMostPopularVideos() {
        List<VideoDto> videos = videoService.findPublishedVideosByPopularity();
        return ResponseEntity.ok(videos);
    }
    // 최신 순으로 정렬(최신순)
    @GetMapping("/latest")
    @CrossOrigin("*")
    public ResponseEntity<List<VideoDto>> getLatestPublishedVideos() {
        List<VideoDto> videos = videoService.findLatestPublishedVideos();
        return ResponseEntity.ok(videos);
    }
    // 필터별로 isPublished가 true인 비디오 정보를 가져옴
    @PostMapping("/filter-videos")
    @CrossOrigin("*")
    public ResponseEntity<List<VideoDto>> getVideosByFilter(@RequestBody Map<String, String> body) {
        String filter = body.get("filter");
        if (filter == null || filter.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<VideoDto> videos = videoService.findPublishedVideosByFilter(filter);
        if (!videos.isEmpty()) {
            return ResponseEntity.ok(videos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // 제목으로 비디오 검색
    @PostMapping("/search-videos")
    @CrossOrigin("*")
    public ResponseEntity<List<VideoDto>> searchVideosByTitle(@RequestBody Map<String, String> body) {
        String videoTitle = body.get("videoTitle");
        if (videoTitle == null || videoTitle.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<VideoDto> videos = videoService.findVideosByTitle(videoTitle);
        if (!videos.isEmpty()) {
            return ResponseEntity.ok(videos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
