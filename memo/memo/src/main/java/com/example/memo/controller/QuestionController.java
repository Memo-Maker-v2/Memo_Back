package com.example.memo.controller;

import com.example.memo.dto.QuestionDto;
import com.example.memo.entity.QuestionEntity;
import com.example.memo.service.QuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/fetch-from-flask")
    @CrossOrigin("*")
    public QuestionEntity saveQuestion(@RequestBody QuestionDto questionDto) throws Exception {
        return questionService.saveOrUpdateQuestion(questionDto);
    }
}