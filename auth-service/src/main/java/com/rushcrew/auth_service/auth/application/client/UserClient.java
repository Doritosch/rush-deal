package com.rushcrew.auth_service.auth.application.client;

import com.rushcrew.auth_service.auth.application.dto.SignUpCommand;
import com.rushcrew.auth_service.auth.application.dto.UserCreateResult;

public interface UserClient {
    UserCreateResult createUser(SignUpCommand command);
}
