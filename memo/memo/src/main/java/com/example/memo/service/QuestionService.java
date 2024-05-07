package com.example.memo.service;

import com.example.memo.dto.QuestionDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.entity.QuestionEntity;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.MemberRepository;
import com.example.memo.repository.QuestionRepository;
import com.example.memo.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;

    public QuestionService(QuestionRepository questionRepository, MemberRepository memberRepository, VideoRepository videoRepository) {
        this.questionRepository = questionRepository;
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
    }

    @Transactional
    public QuestionEntity saveQuestion(QuestionDto questionDto) throws Exception {
        String memberEmail = questionDto.getMemberEmail();  // DTO에서 값 추출
        if (memberEmail == null) {
            throw new IllegalArgumentException("Member email is missing in the request.");
        }

        MemberEntity member = memberRepository.findByMemberEmail(memberEmail);  // 멤버 찾기
        if (member == null) {
            /*throw new UserNotFoundException("User not found: " + memberEmail);  // 멤버가 없을 때 예외 처리*/
            throw new Exception();
        }

        String videoUrl = questionDto.getVideoUrl();
        if (videoUrl == null) {
            throw new IllegalArgumentException("VideoUrl is missing in the request.");
        }

        VideoEntity video = videoRepository.findByVideoUrl(videoUrl);  // 비디오url 찾기
        if (video == null) {
            throw new Exception();
        }

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setQuestion(questionDto.getQuestion());
        questionEntity.setAnswer(questionDto.getAnswer());
        questionEntity.setMemberEmail(questionDto.getMemberEmail());  // 외래 키 설정
        questionEntity.setVideoUrl(questionDto.getVideoUrl());

        return questionRepository.save(questionEntity);
    }
}