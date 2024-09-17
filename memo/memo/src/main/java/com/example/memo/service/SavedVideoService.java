package com.example.memo.service;

import com.example.memo.dto.SavedVideoDto;
import com.example.memo.dto.video.VideoDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.entity.SavedVideoEntity;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.MemberRepository;
import com.example.memo.repository.VideoRepository;
import com.example.memo.service.implement.SavedVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedVideoService {

    @Autowired
    private SavedVideoRepository savedVideoRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<SavedVideoDto> getAllSavedVideos(String memberEmail) {
        List<SavedVideoEntity> savedVideos = savedVideoRepository.findByMember_MemberEmail(memberEmail);

        return savedVideos.stream().map(savedVideo -> {
            VideoEntity video = videoRepository.findById(savedVideo.getVideo().getVideoId()).orElse(null);
            MemberEntity member = memberRepository.findByMemberEmail(savedVideo.getMember().getMemberEmail());

            SavedVideoDto savedVideoDTO = new SavedVideoDto(
                    savedVideo.getSavedId(),
                    savedVideo.getVideo().getVideoId(),
                    savedVideo.getMember().getMemberEmail(),
                    savedVideo.getSavedDate()
            );
            return savedVideoDTO;
        }).collect(Collectors.toList());
    }
    // 좋아요 누른 영상을 저장하는 메서드
    public void saveVideo(String memberEmail, long videoId) {
        // MemberEntity 및 VideoEntity 조회
        MemberEntity member = memberRepository.findByMemberEmail(memberEmail);
        VideoEntity video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));

        // SavedVideoEntity 생성 및 설정
        SavedVideoEntity savedVideo = new SavedVideoEntity();
        savedVideo.setMember(member);
        savedVideo.setVideo(video);
        savedVideo.setSavedDate(LocalDate.now());

        // 저장
        savedVideoRepository.save(savedVideo);
    }
    @Transactional
    // 좋아요 취소 또는 저장된 비디오 삭제 메서드
    public void deleteVideo(String memberEmail, long videoId) {
        // 저장된 비디오가 있는지 확인 후 삭제
        SavedVideoEntity savedVideo = savedVideoRepository.findByVideo_VideoIdAndMember_MemberEmail(videoId, memberEmail);
        if (savedVideo != null) {
            savedVideoRepository.deleteByVideo_VideoIdAndMember_MemberEmail(videoId, memberEmail);
        } else {
            throw new RuntimeException("Saved video not found for deletion");
        }
    }
}
