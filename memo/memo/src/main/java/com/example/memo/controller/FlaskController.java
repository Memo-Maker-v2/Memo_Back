package com.example.memo.controller;


import com.example.memo.dto.QuestionDto;
import com.example.memo.service.FlaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class FlaskController {

    private final FlaskService flaskService;

    /**
     * Flask로 데이터 전송
     */
    @PostMapping("/flask")
    @CrossOrigin("*")
    public String sendToFlask(@RequestBody QuestionDto dto) throws JsonProcessingException {
        return flaskService.sendToFlask(dto);
    }
}
