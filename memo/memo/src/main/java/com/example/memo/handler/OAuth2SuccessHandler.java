package com.example.memo.handler;

import com.example.memo.entity.CustomOAuth2User;
import com.example.memo.provider.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User=(CustomOAuth2User) authentication.getPrincipal();
        String memberEmail=oAuth2User.getMemberEmail() ;
        String token=jwtProvider.create(memberEmail);

        response.sendRedirect("http://localhost:8080/auth/oauth-response/"+token+"/3600");
    }
}
