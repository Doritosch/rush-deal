package com.rushcrew.auth_service.auth.application;

import com.rushcrew.auth_service.auth.application.client.UserClient;
import com.rushcrew.auth_service.auth.application.command.LoginCommand;
import com.rushcrew.auth_service.auth.application.command.SignUpCommand;
import com.rushcrew.auth_service.auth.application.result.LoginResult;
import com.rushcrew.auth_service.auth.application.result.SignUpResult;
import com.rushcrew.auth_service.auth.application.result.TokenPairResult;
import com.rushcrew.auth_service.auth.application.result.UserCreateResult;
import com.rushcrew.auth_service.auth.application.result.VerifyPasswordResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final TokenService tokenService;

    @Transactional
    public SignUpResult signUp(SignUpCommand command) {
        UserCreateResult result = userClient.createUser(command);

        TokenPairResult tokens = tokenService.issueTokenPair(
            result.userId(),
            result.email(),
            result.role()
        );

        return new SignUpResult(
            result.userId(),
            result.email(),
            result.name(),
            tokens.accessToken(),
            tokens.refreshToken()
        );
    }

    public LoginResult login(LoginCommand command) {
        VerifyPasswordResult result = userClient.verifyPassword(command);

        TokenPairResult tokens = tokenService.issueTokenPair(
            result.userId(),
            result.email(),
            result.role()
        );

        return new LoginResult(tokens.accessToken(), tokens.refreshToken());
    }

    @Transactional
    public void logOut(LogoutCommand command) {
        // 토큰 폐기
        tokenService.revokeToken(command.accessToken(), command.refreshToken());
    }

    @Transactional
    public void logoutFromAllDevices(LogoutAllCommand command) {
        // 전체 토큰 폐기
        tokenService.revokeAllTokens(command.userId(), command.accessToken());
    }

    @Transactional
    public String refreshAccessToken(RefreshCommand command) {
        Long userId = tokenService.getUserIdFromRefreshToken(command.refreshToken());

        UserInfoResult user = userClient.getUserById(userId);

        return tokenService.refreshAccessToken(user, command.refreshToken());
    }
}
