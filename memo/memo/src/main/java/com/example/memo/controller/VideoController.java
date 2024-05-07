package com.example.memo.controller;

import com.example.memo.dto.VideoDto;
import com.example.memo.entity.VideoEntity;
import com.example.memo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/video")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }
    //가장 많이 본 video 3개
    @GetMapping("/most-frequent-url")
    @CrossOrigin("*")
    public ResponseEntity<List<String>> getMostFrequentVideoUrls() {
        List<String> urls = videoService.findMostFrequentVideoUrl();
        if (urls != null && !urls.isEmpty()) {
            return ResponseEntity.ok(urls);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //video 정보 추가
    @PostMapping("/save")
    @CrossOrigin("*")
    public VideoEntity saveVideo(@RequestBody VideoDto videoDto) throws Exception{
        return videoService.saveVideo(videoDto);
    }
    //document내용 저장
    @PatchMapping("/document-save")
    @CrossOrigin("*")
    public ResponseEntity<?> updateVideoDocument(
            @RequestParam("memberEmail") String email,
            @RequestParam("videoUrl") String videoUrl,
            @RequestParam("document") String document) {
        try {
            VideoEntity updatedVideo = videoService.updateDocument(email, videoUrl, document);
            return ResponseEntity.ok(updatedVideo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //필기내용 검색
    @GetMapping("/search")
    @CrossOrigin("*")
    public ResponseEntity<List<Long>> searchVideosByKeyword(@RequestParam("keyword") String keyword) {
        List<Long> videoIds = videoService.searchVideosByKeyword(keyword);
        if (!videoIds.isEmpty()) {
            System.out.println(videoIds);
            return ResponseEntity.ok(videoIds);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //video 삭제
    @DeleteMapping("delete-video/{videoId}")
    @CrossOrigin("*")
    public ResponseEntity<Void> deleteCategory(@PathVariable("videoId") long videoId) {
        if (videoService.deleteVideo(videoId)) {
            return ResponseEntity.ok().build(); // 성공적으로 삭제되면 200 OK 응답
        } else {
            return ResponseEntity.notFound().build(); // 해당 ID의 카테고리가 없는 경우 404 Not Found 응답
        }
    }
}
