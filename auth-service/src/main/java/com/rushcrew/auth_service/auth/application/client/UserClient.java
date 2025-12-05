package com.rushcrew.auth_service.auth.application.client;

import com.rushcrew.auth_service.auth.application.command.LoginCommand;
import com.rushcrew.auth_service.auth.application.command.SignUpCommand;
import com.rushcrew.auth_service.auth.application.result.UserCreateResult;
import com.rushcrew.auth_service.auth.application.result.VerifyPasswordResult;

public interface UserClient {
    UserCreateResult createUser(SignUpCommand command);

    VerifyPasswordResult verifyPassword(LoginCommand command);
}
