package com.rushcrew.auth_service.auth.application.client;

import com.rushcrew.auth_service.auth.application.command.SignUpCommand;
import com.rushcrew.auth_service.auth.application.result.UserCreateResult;

public interface UserClient {
    UserCreateResult createUser(SignUpCommand command);
}
