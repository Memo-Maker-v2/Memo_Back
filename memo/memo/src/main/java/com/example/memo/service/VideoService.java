package com.example.memo.service;

import com.example.memo.dto.VideoDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.MemberRepository;
import com.example.memo.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, MemberRepository memberRepository) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public VideoEntity saveVideo(VideoDto videoDto) throws Exception {
        String memberEmail = videoDto.getMemberEmail();
        if (memberEmail == null) {
            throw new IllegalArgumentException("Member email is missing in the request.");
        }

        MemberEntity member = memberRepository.findByMemberEmail(memberEmail);  // 멤버 찾기
        if (member == null) {
            /*throw new UserNotFoundException("User not found: " + memberEmail);  // 멤버가 없을 때 예외 처리*/
            throw new Exception();
        }
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setSummary(videoDto.getSummary());
        videoEntity.setDocument(videoDto.getDocument());
        videoEntity.setVideoUrl(videoDto.getVideoUrl());
        videoEntity.setThumbnailUrl(videoDto.getThumbnailUrl());
        videoEntity.setVideoTitle(videoDto.getVideoTitle());
        videoEntity.setMemberEmail(videoDto.getMemberEmail());
        return videoRepository.save(videoEntity);
    }
    @Transactional
    public VideoEntity updateDocument(String memberEmail, String videoUrl, String newDocument) throws Exception {
        VideoEntity video = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        if (video == null) {
            throw new Exception("Video not found with provided email and URL");
        }
        video.setDocument(newDocument);
        return videoRepository.save(video);
    }
    //가장 많이 검색된 video 3개
    @Transactional(readOnly = true)
    public List<String> findMostFrequentVideoUrl() {
        PageRequest pageRequest = PageRequest.of(0, 3);  // 첫 페이지에서 상위 3개의 결과만 요청
        List<Object[]> results = videoRepository.findMostFrequentVideoUrl(pageRequest);
        return results.stream().map(result -> (String) result[0]).collect(Collectors.toList());
    }

    //필기내용 검색
    @Transactional(readOnly = true)
    public List<Long> searchVideosByKeyword(String keyword) {
        return videoRepository.findVideoIdsByDocumentContaining(keyword);
    }

    //video 삭제
    @Transactional
    public boolean deleteVideo(long videoId) {
        if (videoRepository.existsById(videoId)) {
            videoRepository.deleteById(videoId);
            return true;
        } else {
            return false;
        }
    }
}
