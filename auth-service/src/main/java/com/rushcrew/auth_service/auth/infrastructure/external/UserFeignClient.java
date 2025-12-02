package com.rushcrew.auth_service.auth.infrastructure.external;

import com.rushcrew.auth_service.auth.infrastructure.external.dto.UserCreateRequest;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.UserCreateResponse;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.VerifyPasswordRequest;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.VerifyPasswordResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserFeignClient {
    @PostMapping("/api/v1/users")
    UserCreateResponse createUser(@RequestBody UserCreateRequest request);

    @PostMapping("/api/v1/users/verify-password")
    VerifyPasswordResponse verifyPassword(@RequestBody VerifyPasswordRequest request);
}
