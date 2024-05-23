package com.example.memo.controller;


import com.example.memo.dto.MemberDto;
import com.example.memo.dto.SignInRequestDto;
import com.example.memo.dto.SignInResponseDto;
import com.example.memo.dto.SignUpResponseDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.repository.MemberRepository;
import com.example.memo.service.AuthService;
import com.example.memo.service.implement.OAuth2UserServiceImplement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2UserServiceImplement oAuth2UserService;

    @Autowired
    public AuthController(AuthService authService, MemberRepository memberRepository, OAuth2UserServiceImplement oAuth2UserService, ClientRegistrationRepository clientRegistrationRepository) {
        this.authService = authService;
        this.memberRepository = memberRepository;
        this.oAuth2UserService = oAuth2UserService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }
    @PostMapping("/sign-up")
    public ResponseEntity<? super SignUpResponseDto> signUp(
            @RequestBody @Valid MemberDto requestBody
    ) {
        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestBody);
        return response;
    }

    @PostMapping("/sign-in")
    @CrossOrigin("*")
    public ResponseEntity<? super SignInResponseDto> signIn(
            @RequestBody @Valid SignInRequestDto requestBody
    ) {
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestBody);
        return response;
    }
    // 카카오 OAuth2 로그인 엔드포인트
    @PostMapping("/oauth2/kakao")
    @CrossOrigin("*")
    public ResponseEntity<?> kakaoOAuth2Login(@RequestBody Map<String, String> payload) throws JsonProcessingException {
        String code = payload.get("code");

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("kakao");

        // Requesting access token
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientRegistration.getClientId());
        params.add("redirect_uri", clientRegistration.getRedirectUri());
        params.add("code", code);
        params.add("client_secret", clientRegistration.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", request, String.class);

        // Extracting access token
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
        String accessToken = (String) responseMap.get("access_token");

        // Requesting user info
        headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> userInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> userInfoResponse = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, userInfoRequest, String.class);

        // Processing user info
        String userInfoResponseBody = userInfoResponse.getBody();
        Map<String, Object> userInfoMap = objectMapper.readValue(userInfoResponseBody, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfoMap.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        if (email == null) {
            System.err.println("Email is null. Cannot create MemberEntity without email.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found in Kakao response");
        }
        // Check if the user already exists in the database
        if (memberRepository.existsByMemberEmail(email)) {
            System.out.println("User already exists with email: " + email);
            return ResponseEntity.ok("duplicate");
        }
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = profile != null ? (String) profile.get("nickname") : "Unknown";

        // Creating and saving member entity
        MemberEntity memberEntity = new MemberEntity(email, nickname);
//        memberRepository.save(memberEntity);
        System.out.println(memberEntity.getMemberEmail());
//        return ResponseEntity.ok(userInfoMap);
        return ResponseEntity.ok(memberEntity);
    }
}
