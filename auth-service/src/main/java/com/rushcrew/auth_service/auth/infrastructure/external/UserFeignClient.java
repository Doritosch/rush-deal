package com.rushcrew.auth_service.auth.infrastructure.external;

import com.rushcrew.auth_service.auth.infrastructure.external.dto.UserCreateRequest;
import com.rushcrew.auth_service.auth.infrastructure.external.dto.UserCreateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserFeignClient {
    @PostMapping("/api/v1/users/signup")
    UserCreateResponse createUser(@RequestBody UserCreateRequest UserCreateRequest);
}
