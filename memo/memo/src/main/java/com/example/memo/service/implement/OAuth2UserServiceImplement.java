package com.example.memo.service.implement;

import com.example.memo.entity.CustomOAuth2User;
import com.example.memo.entity.MemberEntity;
import com.example.memo.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImplement extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthorizationException{
        OAuth2User oAuth2User=super.loadUser(request);
        String oauthClientName=request.getClientRegistration().getClientName();
        try {
            System.out.println(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        }catch (Exception exception){
            exception.printStackTrace();
        }
        MemberEntity memberEntity=null;
//        Map<String,Object> kakao_account =(Map<String,Object>) oAuth2User.getAttributes().get("kakao_account");
//        Map<String,Object> profile =(Map<String,Object>) kakao_account.get("profile");
        String userId="kakao_"+oAuth2User.getAttributes().get("id");
//        String email="kakao_"+oAuth2User.getAttributes().get("email");
//        String name="kakao_"+oAuth2User.getAttributes().get("nickname");

//        String email= (String) profile.get("email");
//        String name= (String) profile.get("nickname");
        memberEntity=new MemberEntity(userId);

        memberRepository.save(memberEntity);
        return new CustomOAuth2User(userId);
    }
}
