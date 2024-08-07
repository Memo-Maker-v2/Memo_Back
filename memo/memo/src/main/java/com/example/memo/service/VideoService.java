package com.example.memo.service;

import com.example.memo.dto.QuestionDto;
import com.example.memo.dto.video.VideoAndQuestionDto;
import com.example.memo.dto.video.VideoDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.entity.QuestionEntity;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.MemberRepository;
import com.example.memo.repository.QuestionRepository;
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
    private final QuestionRepository questionRepository;
    @Autowired
    public VideoService(VideoRepository videoRepository, MemberRepository memberRepository,QuestionRepository questionRepository) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
        this.questionRepository=questionRepository;
    }
    // 동일한 videoUrl과 memberEmail을 가진 비디오가 존재하는지 확인
    @Transactional(readOnly = true)
    public boolean videoExists(String memberEmail, String videoUrl) {
        return videoRepository.existsByVideoUrlAndMemberEmail(videoUrl, memberEmail);
    }

    //영상요약시 영상정보 저장
    @Transactional
    public VideoEntity saveVideo(VideoDto videoDto) throws Exception {
        String memberEmail = videoDto.getMemberEmail();
        String videoUrl = videoDto.getVideoUrl();
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
        videoEntity.setFullScript(videoDto.getFullScript());
        videoEntity.setVideoUrl(videoDto.getVideoUrl());
        videoEntity.setThumbnailUrl(videoDto.getThumbnailUrl());
        videoEntity.setVideoTitle(videoDto.getVideoTitle());
        videoEntity.setFilter(videoDto.getFilter());
        videoEntity.setMemberEmail(videoDto.getMemberEmail());
        videoEntity.setDocumentDate(videoDto.getDocumentDate());
        return videoRepository.save(videoEntity);
    }
    //가장 많이 검색된 video 3개
    @Transactional(readOnly = true)
    public List<VideoDto> findMostFrequentVideos() {
        PageRequest pageRequest = PageRequest.of(0, 3); // 첫 번째 페이지에 3개의 결과
        List<Object[]> results = videoRepository.findMostFrequentVideos(pageRequest);
        return results.stream()
                .map(result -> new VideoDto((String) result[0], (String) result[1], (String) result[2]))
                .collect(Collectors.toList());
    }
    // videoUrl과 memberEmail을 사용하여 FullScript 데이터를 가져오는 메서드
    @Transactional(readOnly = true)
    public String getFullScriptByMemberEmailAndVideoUrl(String memberEmail, String videoUrl) {
        VideoEntity videoEntity = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        if (videoEntity != null) {
            return videoEntity.getFullScript();
        } else {
            throw new IllegalStateException("Video not found for the provided email and URL");
        }
    }
    //video와 question 정보 가져옴
    @Transactional(readOnly = true)
    public VideoAndQuestionDto fetchVideoAndQuestions(String memberEmail, String videoUrl) {
        VideoEntity video = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        if (video == null) {
            throw new IllegalStateException("Video not found for the provided email and URL");
        }

        List<QuestionEntity> questions = questionRepository.findByVideoUrlAndMemberEmail(videoUrl, memberEmail);
        List<QuestionDto> questionDtos = questions.stream()
                .map(q -> new QuestionDto(q.getQuestion(), q.getAnswer(),q.getMemberEmail(),q.getVideoUrl()))
                .collect(Collectors.toList());

        VideoDto videoDto = new VideoDto(video.getVideoTitle(),video.getSummary(),video.getFullScript(), video.getVideoUrl(),video.getMemberEmail(), video.getDocumentDate(), video.getCategoryName());
        return new VideoAndQuestionDto(videoDto, questionDtos);
    }
    // 비디오의 filter 정보를 업데이트하는 메서드
    @Transactional
    public void updateVideoFilter(String memberEmail, String videoUrl, String newFilter) {
        VideoEntity videoEntity = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        if (videoEntity == null) {
            throw new IllegalStateException("Video not found for the provided email and URL");
        }

        videoEntity.setFilter(newFilter);
        videoRepository.save(videoEntity);
    }
    //categoryName과 memberEmail로 영상 조회
    @Transactional(readOnly = true)
    public List<VideoDto> findVideosByCategoryAndMemberEmail(String categoryName, String memberEmail) {
        List<VideoEntity> videos = videoRepository.findByCategoryNameAndMemberEmail(categoryName, memberEmail);
        return videos.stream()
                .map(video -> new VideoDto(video.getVideoUrl(), video.getThumbnailUrl(), video.getVideoTitle(),video.getCategoryName()))
                .collect(Collectors.toList());
    }
    //memberEmail로 모든 영상을 조회
    public List<VideoDto> findVideosByMemberEmail(String memberEmail) {
        List<VideoEntity> videos = videoRepository.findByMemberEmail(memberEmail);
        return videos.stream()
                .map(video -> new VideoDto(video.getVideoUrl(), video.getThumbnailUrl(), video.getVideoTitle(), video.getCategoryName()))
                .collect(Collectors.toList());
    }
    // 비디오의 isPublished 값을 업데이트하는 메서드
    public void updateVideoPublicationStatus(String memberEmail, String videoUrl, boolean isPublished) throws IllegalStateException {
        VideoEntity videoEntity = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        videoEntity.setIsPublished(isPublished);
        videoRepository.save(videoEntity);
    }
    // 필터와 isPublished가 true인 비디오 정보를 가져오는 메서드
    public List<VideoDto> findPublishedVideosByFilter(String filter) {
        List<VideoEntity> videoEntities = videoRepository.findByFilterAndIsPublishedTrue(filter);
        return videoEntities.stream()
                .map(VideoDto::new) // VideoEntity를 VideoDto로 변환
                .collect(Collectors.toList());
    }
    //카테고리에 영상 추가
    @Transactional
    public void addVideoToFolder(String memberEmail, String videoUrl, String categoryName) {
        VideoEntity video = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);

        if (video == null) {
            // 해당 비디오를 찾지 못한 경우에 대한 예외 처리
            throw new IllegalArgumentException("Video not found for the provided memberEmail and videoUrl");
        }

        // 비디오를 찾은 경우에는 카테고리를 업데이트하고 저장합니다.
        video.setCategoryName(categoryName);
        videoRepository.save(video);
    }
    // video_table의 카테고리 이름 업데이트
    @Transactional
    public void updateCategoryNameForMember(String memberEmail, String oldCategoryName, String newCategoryName) {
        List<VideoEntity> videos = videoRepository.findByMemberEmailAndCategoryName(memberEmail, oldCategoryName);
        for (VideoEntity video : videos) {
            video.setCategoryName(newCategoryName);
            videoRepository.save(video);
        }
    }
    //카테고리 삭제시 categoryName null로 해줌
    @Transactional
    public void removeCategoryFromVideos(String memberEmail, String categoryName) {
        List<VideoEntity> videos = videoRepository.findByMemberEmailAndCategoryName(memberEmail, categoryName);
        for (VideoEntity video : videos) {
            video.setCategoryName(null);
            videoRepository.save(video);
        }
    }
    //video 삭제
    @Transactional
    public boolean deleteVideo(String memberEmail, String videoUrl) {
        VideoEntity video = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        if (video != null) {
            videoRepository.delete(video);
            return true;
        } else {
            return false; // 비디오를 찾지 못한 경우
        }
    }
    @Transactional
    public List<VideoDto> findPublishedVideos() {
        List<VideoEntity> videoEntities = videoRepository.findByIsPublishedTrue();
        return videoEntities.stream()
                .map(videoEntity -> new VideoDto(
                        videoEntity.getVideoUrl(),
                        videoEntity.getThumbnailUrl(),
                        videoEntity.getVideoTitle(),
                        videoEntity.getCategoryName(),
                        videoEntity.getFilter(),
                        videoEntity.getDocumentDate(),
                        videoEntity.getIsPublished(),
                        videoEntity.getViewCount()))
                .collect(Collectors.toList());
    }
    // memberEmail과 videoUrl을 사용하여 VideoDto를 가져오는 메서드
    @Transactional
    public VideoDto findVideoByEmailAndUrl(String memberEmail, String videoUrl) {
        VideoEntity videoEntity = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        if (videoEntity != null) {
            videoRepository.incrementViewCount(memberEmail, videoUrl);
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
        return null;
    }
    // isPublished가 true인 비디오를 조회수 기준으로 정렬하여 가져오는 메서드
    @Transactional(readOnly = true)
    public List<VideoDto> findPublishedVideosByPopularity() {
        List<VideoEntity> videoEntities = videoRepository.findByIsPublishedTrueOrderByViewCountDesc();
        return videoEntities.stream()
                .map(videoEntity -> new VideoDto(
                        videoEntity.getVideoUrl(),
                        videoEntity.getThumbnailUrl(),
                        videoEntity.getVideoTitle(),
                        videoEntity.getCategoryName(),
                        videoEntity.getFilter(),
                        videoEntity.getDocumentDate(),
                        videoEntity.getIsPublished(),
                        videoEntity.getViewCount()
                ))
                .collect(Collectors.toList());
    }
}