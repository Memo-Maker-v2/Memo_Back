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

import java.util.List;
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

            // 필요한 경우 추가 정보를 설정합니다.
            if (video != null) {
                savedVideoDTO.setVideoTitle(video.getVideoTitle());
            }

            if (member != null) {
                savedVideoDTO.setMemberName(member.getMemberName());
            }

            return savedVideoDTO;
        }).collect(Collectors.toList());
    }
    //비디오 조회하고 조회수를 증가시킴
    @Transactional
    public Optional<VideoDto> getVideoById(long videoId) {
        videoRepository.incrementViewCount(videoId); // 조회수 증가

        Optional<VideoEntity> videoEntity = videoRepository.findById(videoId);
        return videoEntity.map(this::convertToDto);
    }

    private VideoDto convertToDto(VideoEntity videoEntity) {
        return new VideoDto(
                videoEntity.getVideoUrl(),
                videoEntity.getThumbnailUrl(),
                videoEntity.getVideoTitle(),
                videoEntity.getCategoryName(),
                videoEntity.getFilter(),
                videoEntity.getDocumentDate(),
                videoEntity.getIsPublished(),
                videoEntity.getViewCount()

        );
    }
}
