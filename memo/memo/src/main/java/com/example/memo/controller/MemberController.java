package com.example.memo.controller;

import com.example.memo.dto.MemberDTO;
import com.example.memo.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor//MemberService에 대한 멤버 사용 가능
public class MemberController {
    //생성자 주입
    private final MemberService memberService;
    //회원가입 페이지 출력 요청
    @GetMapping("/member/save") //signup
    public String saveForm(){
        return "save";
    }
    @PostMapping("/member/save")
    @CrossOrigin("*")
    //name값을 requestparam에 담아옴
    public String save(@RequestBody MemberDTO memberDTO){
        System.out.println("MemberController.save");
        System.out.println("memberDTO = " + memberDTO);
        memberService.save(memberDTO);//memberServier에 memberDTO 전달
        return "login";
    }
    @GetMapping("/member/login")
    public String loginForm(){
        return "login";
    }
    @PostMapping("/member/login")//session: 로그인 유지
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session){
        MemberDTO loginResult=memberService.login(memberDTO);
        if (loginResult != null) {//login 성공
            session.setAttribute("loginEmail",loginResult.getMemberEmail());
//            session.setAttribute("loginName",loginResult.getMemberName());
            return "main";
        }else{//login 실패
            return "login";
        }
    }
}
