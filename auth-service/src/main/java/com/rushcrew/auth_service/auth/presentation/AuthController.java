package com.rushcrew.auth_service.auth.presentation;

import com.rushcrew.auth_service.auth.application.AuthService;
import com.rushcrew.auth_service.auth.application.command.LoginCommand;
import com.rushcrew.auth_service.auth.application.command.LogoutAllCommand;
import com.rushcrew.auth_service.auth.application.command.LogoutCommand;
import com.rushcrew.auth_service.auth.application.command.RefreshCommand;
import com.rushcrew.auth_service.auth.application.command.SignUpCommand;
import com.rushcrew.auth_service.auth.application.result.LoginResult;
import com.rushcrew.auth_service.auth.application.result.SignUpResult;
import com.rushcrew.auth_service.auth.presentation.dto.request.LoginRequest;
import com.rushcrew.auth_service.auth.presentation.dto.request.SignUpRequest;
import com.rushcrew.auth_service.auth.presentation.dto.response.LoginResponse;
import com.rushcrew.auth_service.auth.presentation.dto.response.RefreshAccessTokenResponse;
import com.rushcrew.auth_service.auth.presentation.dto.response.SignUpResponse;
import com.rushcrew.auth_service.auth.presentation.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenUtils tokenUtils;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(
        @Valid @RequestBody SignUpRequest request
    ) {
        SignUpCommand command = request.toCommand();

        SignUpResult result = authService.signUp(command);

        URI location = URI.create("/api/v1/users/" + result.userId());

        return ResponseEntity.created(location).body(SignUpResponse.fromResult(result));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletResponse response
    ) {
        LoginCommand command = request.toCommand();

        LoginResult result = authService.login(command);

        ResponseCookie refreshCookie = tokenUtils.createRefreshTokenCookie(result.refreshToken());

        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok(LoginResponse.fromResult(result));
    }

    /**
     * 로그아웃 - Access Token 블랙리스트 추가 및 Refresh Token 폐기
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(
        @RequestHeader("Authorization") String authorization,
        HttpServletRequest request
    ) {
        String accessToken = tokenUtils.extractAccessToken(authorization);
        String refreshToken = tokenUtils.extractRefreshToken(request);

        LogoutCommand command = new LogoutCommand(accessToken, refreshToken);

        authService.logOut(command);

        ResponseCookie expiredCookie = tokenUtils.createExpiredRefreshTokenCookie();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
            .build();
    }

    /**
     * 모든 기기에서 로그아웃
     */
    @PostMapping("/logout/all")
    public ResponseEntity<Void> logoutAll(
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("Authorization") String authorization
    ) {
        String accessToken = tokenUtils.extractAccessToken(authorization);

        LogoutAllCommand command = new LogoutAllCommand(userId, accessToken);

        authService.logoutFromAllDevices(command);

        ResponseCookie expiredCookie = tokenUtils.createExpiredRefreshTokenCookie();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
            .build();
    }

    /**
     * Access 토큰 초기화
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshAccessTokenResponse> refreshAccessToken(
        HttpServletRequest request
    ) {
        String refreshToken = tokenUtils.extractRefreshToken(request);

        RefreshCommand command = new RefreshCommand(refreshToken);

        String accessToken = authService.refreshAccessToken(command);

        return ResponseEntity.ok(RefreshAccessTokenResponse.of(accessToken));
    }
}
