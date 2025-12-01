package com.rushcrew.auth_service.auth.infrastructure.external;

import com.rushcrew.auth_service.auth.application.client.UserClient;
import com.rushcrew.auth_service.auth.application.command.SignUpCommand;
import com.rushcrew.auth_service.auth.application.result.UserCreateResult;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.UserCreateRequest;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.UserCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

    private final UserFeignClient userFeignClient;

    @Override
    public UserCreateResult createUser(SignUpCommand command) {
        UserCreateRequest request = UserCreateRequest.from(command);
        UserCreateResponse response = userFeignClient.createUser(request);

        return new UserCreateResult(
            response.userId(),
            response.email(),
            response.name(),
            response.role()
        );
    }
}
