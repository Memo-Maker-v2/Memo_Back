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

import java.util.List;

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
    public QuestionEntity saveOrUpdateQuestion(QuestionDto questionDto) throws Exception {
        String memberEmail = questionDto.getMemberEmail();  // DTO에서 값 추출
        System.out.println("saveOrUpdateQuestion qmemberEmail = " + memberEmail);
        if (memberEmail == null) {
            throw new IllegalArgumentException("Member email is missing in the request.");
        }
        
        MemberEntity member = memberRepository.findByMemberEmail(memberEmail);  // 멤버 찾기
        if (member == null) {
            throw new Exception("User not found: " + memberEmail);  // 멤버가 없을 때 예외 처리
        }
        System.out.println("member = " + member);
        
        String videoUrl = questionDto.getVideoUrl();
        if (videoUrl == null) {
            throw new IllegalArgumentException("VideoUrl is missing in the request.");
        }
        System.out.println("videoUrl = " + videoUrl);
        
        // 비디오를 찾기
        VideoEntity video = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        if (video == null) {
            throw new Exception("Video not found for memberEmail: " + memberEmail + " and videoUrl: " + videoUrl);
        }
        System.out.println("video = " + video);
        
        // 기존 질문이 있는지 확인
        List<QuestionEntity> existingQuestions = questionRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        QuestionEntity questionEntity;
        
        if (!existingQuestions.isEmpty()) {
            // 질문이 있으면 업데이트
            questionEntity = existingQuestions.get(0);
            questionEntity.setQuestion(questionDto.getQuestion());
            questionEntity.setAnswer(questionDto.getAnswer());
            System.out.println("Question updated: " + questionEntity);
        } else {
            // 질문이 없으면 새로 추가
            questionEntity = new QuestionEntity();
            questionEntity.setQuestion(questionDto.getQuestion());
            questionEntity.setAnswer(questionDto.getAnswer());
            questionEntity.setMemberEmail(memberEmail);
            questionEntity.setVideoUrl(videoUrl);
            System.out.println("New question created: " + questionEntity);
        }
        
        return questionRepository.save(questionEntity);
    }
    
    
}