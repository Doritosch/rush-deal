package com.rushcrew.auth_service.auth.infrastructure.external;

import com.rushcrew.auth_service.auth.application.client.UserClient;
import com.rushcrew.auth_service.auth.application.command.LoginCommand;
import com.rushcrew.auth_service.auth.application.command.SignUpCommand;
import com.rushcrew.auth_service.auth.application.result.UserCreateResult;
import com.rushcrew.auth_service.auth.application.result.VerifyPasswordResult;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.UserCreateRequest;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.UserCreateResponse;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.VerifyPasswordRequest;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.VerifyPasswordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

    private final UserFeignClient userFeignClient;

    @Override
    public UserCreateResult createUser(SignUpCommand command) {
        UserCreateRequest request = UserCreateRequest.fromCommand(command);

        UserCreateResponse response = userFeignClient.createUser(request);

        return response.toResult();
    }

    @Override
    public VerifyPasswordResult verifyPassword(LoginCommand command) {
        VerifyPasswordRequest request = VerifyPasswordRequest.fromCommand(command);

        VerifyPasswordResponse response = userFeignClient.verifyPassword(request);

        return response.toResult();
    }
}
