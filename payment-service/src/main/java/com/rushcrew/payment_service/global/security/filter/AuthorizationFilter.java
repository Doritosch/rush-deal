package com.rushcrew.payment_service.global.security.filter;

import com.rushcrew.payment_service.global.security.model.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
// OncePerRequestFilter를 상속받아 하나의 요청당 한 번만 필터가 실행되도록 보장
public class AuthorizationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    // 실제 필터링 로직을 구현하는 메서드
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String userId = request.getHeader(USER_ID_HEADER);
        String userName = request.getHeader(USER_NAME_HEADER);
        String role = request.getHeader(USER_ROLE_HEADER);

        if (userId == null || userName == null || role == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 추출한 정보로 Spring Security의 UserDetails 객체 생성
        UserDetailsImpl userDetails = new UserDetailsImpl(
                Long.valueOf(userId),
                userName,
                role
        );

        // UserDetails를 Spring Security의 인증 객체로 변환
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        // 인증 객체에 HTTP 요청의 세부 정보(IP, 세션 ID 등)를 추가
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        // 현재 스레드의 SecurityContext에 인증 정보를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
