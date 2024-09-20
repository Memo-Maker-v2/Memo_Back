package com.example.memo.controller;

import com.example.memo.dto.SavedVideoDto;
import com.example.memo.dto.video.VideoDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.repository.MemberRepository;
import com.example.memo.service.SavedVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/video")
public class SavedVideoController {
    @Autowired
    private SavedVideoService savedVideoService;

    @Autowired
    private MemberRepository memberRepository;
    @PostMapping("/saved-videos")
    public List<SavedVideoDto> getSavedVideos(@RequestBody Map<String, String> request) {
        String memberEmail = request.get("memberEmail");
        return savedVideoService.getAllSavedVideos(memberEmail);
    }
    // 좋아요 버튼을 눌렀을 때 영상을 저장하는 엔드포인트
    @PostMapping("/like")
    public String likeVideo(@RequestBody Map<String, String> request) {
        String memberEmail = request.get("memberEmail");
        long videoId = Long.parseLong(request.get("videoId"));

        savedVideoService.saveVideo(memberEmail, videoId);
        return "Video liked and saved!";
    }
    // 좋아요 취소 또는 비디오 삭제 엔드포인트
    @DeleteMapping("/unlike")
    public String unlikeVideo(@RequestBody Map<String, String> request) {
        String memberEmail = request.get("memberEmail");
        long videoId = Long.parseLong(request.get("videoId"));

        savedVideoService.deleteVideo(memberEmail, videoId);
        return "Video unliked and deleted!";
    }
}
