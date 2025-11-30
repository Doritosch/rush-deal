package com.rushcrew.auth_service.auth.presentation;

import com.rushcrew.auth_service.auth.application.AuthService;
import com.rushcrew.auth_service.auth.application.command.SignUpCommand;
import com.rushcrew.auth_service.auth.application.result.SignUpResult;
import com.rushcrew.auth_service.auth.presentation.dto.request.SignUpRequest;
import com.rushcrew.auth_service.auth.presentation.dto.response.SignUpResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(
        @Valid @RequestBody SignUpRequest request
    ) {
        SignUpCommand command = request.toCommand();

        SignUpResult result = authService.signUp(command);

        URI location = URI.create("/api/v1/users/" + result.userId());

        return ResponseEntity.created(location).body(SignUpResponse.from(result));
    }
}
