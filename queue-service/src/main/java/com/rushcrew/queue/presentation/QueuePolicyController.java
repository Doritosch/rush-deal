package com.rushcrew.queue.presentation;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.presentation.dto.request.CreatePolicyRequest;
import com.rushcrew.queue.presentation.dto.response.CreatePolicyResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/queue/policies")
public class QueuePolicyController {

    // API Gateway에서 인증 후, USER ID와 ROLE을 헤더에 담아 전달
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    public QueuePolicyController() {}

    /**
     * 타임딜 정책 생성 : MASTER 권한만 가능
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePolicyResponse>> registerPolicy(
        @Valid @RequestBody CreatePolicyRequest request
    ) {
        CreatePolicyCommand command = request.toCommand();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("TODO"));
    }




}
