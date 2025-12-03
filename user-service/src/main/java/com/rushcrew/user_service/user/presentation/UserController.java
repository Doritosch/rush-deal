package com.rushcrew.user_service.user.presentation;

import com.rushcrew.user_service.user.application.UserService;
import com.rushcrew.user_service.user.application.command.UserCreateCommand;
import com.rushcrew.user_service.user.application.command.VerifyPasswordCommand;
import com.rushcrew.user_service.user.application.result.UserCreateResult;
import com.rushcrew.user_service.user.application.result.UserResult;
import com.rushcrew.user_service.user.application.result.VerifyPasswordResult;
import com.rushcrew.user_service.user.presentation.dto.request.UserCreateRequest;
import com.rushcrew.user_service.user.presentation.dto.request.VerifyPasswordRequest;
import com.rushcrew.user_service.user.presentation.dto.response.UserCreateResponse;
import com.rushcrew.user_service.user.presentation.dto.response.UserResponse;
import com.rushcrew.user_service.user.presentation.dto.response.VerifyPasswordResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserCreateResponse> createUser(
        @Valid @RequestBody UserCreateRequest request
    ) {
        UserCreateCommand command = request.toCommand();

        UserCreateResult result = userService.createUser(command);
        
        URI location =  URI.create("/api/v1/users/" + result.userId());

        return ResponseEntity.created(location).body(UserCreateResponse.from(result));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<VerifyPasswordResponse> verifyPassword(
        @RequestBody VerifyPasswordRequest request
    ) {
        VerifyPasswordCommand command = request.toCommand();

        VerifyPasswordResult result = userService.verifyPassword(command);

        return ResponseEntity.ok(VerifyPasswordResponse.fromResult(result));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(
        @RequestHeader("X-User-Id") Long userId
    ) {
        UserResult result = userService.getUser(userId);
        return ResponseEntity.ok(UserResponse.fromResult(result));
    }

}
