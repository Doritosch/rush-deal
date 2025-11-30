package com.rushcrew.auth_service.auth.application;

import com.rushcrew.auth_service.auth.application.client.UserClient;
import com.rushcrew.auth_service.auth.application.command.SignUpCommand;
import com.rushcrew.auth_service.auth.application.port.TokenProvider;
import com.rushcrew.auth_service.auth.application.result.SignUpResult;
import com.rushcrew.auth_service.auth.application.result.UserCreateResult;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserClient userClient;
    private final TokenProvider accessTokenProvider;

    @Transactional
    public SignUpResult signUp(SignUpCommand command) {
        UserCreateResult result = userClient.createUser(command);

        String accessToken = accessTokenProvider.generateToken(
            result.userId(),
            result.email(),
            result.role()
        );

        return new SignUpResult(
            result.userId(),
            result.email(),
            result.name(),
            accessToken,
            new Date()
        );
    }
}
