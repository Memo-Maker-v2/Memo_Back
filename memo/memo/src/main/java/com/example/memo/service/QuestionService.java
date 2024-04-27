package com.example.memo.service;

import com.example.memo.dto.QuestionDto;
import com.example.memo.entity.QuestionEntity;
import com.example.memo.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional
    public QuestionEntity saveQuestion(QuestionDto questionDto) {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setQuestion(questionDto.getQuestion());
        questionEntity.setAnswer(questionDto.getAnswer());

        return questionRepository.save(questionEntity); // 데이터베이스에 저장
    }
}
