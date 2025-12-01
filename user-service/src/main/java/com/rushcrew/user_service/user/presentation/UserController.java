package com.rushcrew.user_service.user.presentation;

import com.rushcrew.user_service.user.application.UserService;
import com.rushcrew.user_service.user.application.command.UserCreateCommand;
import com.rushcrew.user_service.user.application.result.UserCreateResult;
import com.rushcrew.user_service.user.presentation.dto.request.UserCreateRequest;
import com.rushcrew.user_service.user.presentation.dto.response.UserCreateResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponse> signUp(
        @Valid @RequestBody UserCreateRequest request
    ) {
        UserCreateCommand command = request.toCommand();

        UserCreateResult result = userService.signUp(command);

        URI location =  URI.create("/api/v1/users/" + result.userId());

        return ResponseEntity.created(location).body(UserCreateResponse.from(result));
    }
}
