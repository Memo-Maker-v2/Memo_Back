package com.example.memo.service.implement;

import com.example.memo.entity.CustomOAuth2User;
import com.example.memo.entity.MemberEntity;
import com.example.memo.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImplement extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserServiceImplement.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthorizationException {

        OAuth2User oAuth2User = super.loadUser(request);
        String oauthClientName = request.getClientRegistration().getClientName();

        // 추가된 로그: oAuth2User의 attributes 확인
        logger.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

        // 카카오 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");

        String name = (String) profile.get("nickname");
        String memberEmail = (String) kakao_account.get("email");
        MemberEntity memberEntity = new MemberEntity(memberEmail, name);

        logger.info("Saving member with email: {} and name: {}", memberEmail, name);
        memberRepository.save(memberEntity);
        logger.info("Saved memberEntity: {}", memberEntity);

        // 새로운 속성 맵 생성
        Map<String, Object> userAttributes = new HashMap<>(oAuth2User.getAttributes());
        userAttributes.put("memberEmail", memberEmail);

        // 권한 설정 (필요한 경우)

        return new CustomOAuth2User(userAttributes, memberEmail);
    }
}
