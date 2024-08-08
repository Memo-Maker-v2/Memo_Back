package com.example.memo.controller;

import com.example.memo.dto.video.VideoAndQuestionDto;
import com.example.memo.dto.video.VideoDto;
import com.example.memo.entity.VideoEntity;
import com.example.memo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<VideoDto>> getMostFrequentVideos() {
        List<VideoDto> videos = videoService.findMostFrequentVideos();
        if (videos != null && !videos.isEmpty()) {
            return ResponseEntity.ok(videos);
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
    //영상 클릭했을 때 영상 정보 불러옴
    @PostMapping("select-video")
    @CrossOrigin("*")
    public ResponseEntity<VideoAndQuestionDto> fetchVideoAndQuestions(@RequestBody VideoDto videoRequest) {
        try {
            VideoAndQuestionDto videoAndQuestions = videoService.fetchVideoAndQuestions(videoRequest.getMemberEmail(), videoRequest.getVideoUrl());
            return ResponseEntity.ok(videoAndQuestions);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    // videoUrl과 memberEmail을 사용하여 FullScript 데이터를 가져옴
    @PostMapping("/fullscript")
    @CrossOrigin("*")
    public ResponseEntity<String> getFullScript(@RequestBody Map<String, String> requestBody) {
        String memberEmail = requestBody.get("memberEmail");
        String videoUrl = requestBody.get("videoUrl");

        if (memberEmail == null || videoUrl == null) {
            return ResponseEntity.badRequest().body("Missing memberEmail or videoUrl");
        }

        try {
            String fullScript = videoService.getFullScriptByMemberEmailAndVideoUrl(memberEmail, videoUrl);
            return ResponseEntity.ok(fullScript);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // 비디오의 filter 정보를 업데이트
    @PutMapping("/update-filter")
    @CrossOrigin("*")
    public ResponseEntity<String> updateVideoFilter(@RequestBody Map<String, String> requestBody) {
        String memberEmail = requestBody.get("memberEmail");
        String videoUrl = requestBody.get("videoUrl");
        String newFilter = requestBody.get("filter");

        if (memberEmail == null || videoUrl == null || newFilter == null) {
            return ResponseEntity.badRequest().body("Missing memberEmail, videoUrl, or filter");
        }

        try {
            videoService.updateVideoFilter(memberEmail, videoUrl, newFilter);
            return ResponseEntity.ok("Filter updated successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // 비디오의 공개 여부 업데이트
    @PutMapping("/update-publication-status")
    @CrossOrigin("*")
    public ResponseEntity<String> updateVideoPublicationStatus(@RequestBody Map<String, String> requestBody) {
        String memberEmail = requestBody.get("memberEmail");
        String videoUrl = requestBody.get("videoUrl");
        String status = requestBody.get("status");

        if (memberEmail == null || videoUrl == null || status == null) {
            return ResponseEntity.badRequest().body("Missing memberEmail, videoUrl, or status");
        }

        boolean isPublished;
        if (status.equalsIgnoreCase("public")) {
            isPublished = true;
        } else if (status.equalsIgnoreCase("private")) {
            isPublished = false;
        } else {
            return ResponseEntity.badRequest().body("Invalid status value");
        }

        try {
            videoService.updateVideoPublicationStatus(memberEmail, videoUrl, isPublished);
            return ResponseEntity.ok("Publication status updated successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.ok("Video not found");
        }
    }

    // 멤버 이메일에 따른 비디오 목록 조회
    @PostMapping("/category-video")
    @CrossOrigin("*")
    public ResponseEntity<?> getVideosByCategory(@RequestBody Map<String, String> body) {
        String memberEmail = body.get("memberEmail");
        String categoryName = body.get("categoryName");
        if (memberEmail == null || memberEmail.isEmpty()) {
            return ResponseEntity.ok(false);
        }
        try {
            List<VideoDto> videos;
            if (categoryName == null || categoryName.isEmpty()) {
                // categoryName이 null이거나 비어 있을 때, memberEmail로 모든 비디오를 조회
                videos = videoService.findVideosByMemberEmail(memberEmail);
            } else {
                // categoryName과 memberEmail로 비디오를 조회
                videos = videoService.findVideosByCategoryAndMemberEmail(categoryName, memberEmail);
            }
            if (!videos.isEmpty()) {
                return ResponseEntity.ok(videos);
            } else {
                return ResponseEntity.ok(false);
            }
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
    //video삭제
    @DeleteMapping("/delete-video")
    @CrossOrigin("*")
    public ResponseEntity<?> deleteVideo(@RequestBody VideoDto videoDto) {
        if (videoService.deleteVideo(videoDto.getMemberEmail(), videoDto.getVideoUrl())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.ok(false);
        }
    }
    //중복처리
    @PostMapping("/check-duplicate")
    @CrossOrigin("*")
    public ResponseEntity<Boolean> checkVideoDuplicate(@RequestBody VideoDto videoDto) {
        boolean exists = videoService.videoExists(videoDto.getMemberEmail(), videoDto.getVideoUrl());
        return ResponseEntity.ok(exists);
    }
}
