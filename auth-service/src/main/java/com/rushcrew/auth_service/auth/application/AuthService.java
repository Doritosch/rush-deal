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
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        return new LoginResult(
            result.userId(),
            result.email(),
            result.name(),
            tokens.accessToken(),
            tokens.refreshToken()
        );
    }
}
