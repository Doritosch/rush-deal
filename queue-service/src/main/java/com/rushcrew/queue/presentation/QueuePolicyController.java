package com.rushcrew.queue.presentation;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.application.command.SearchPolicyCommand;
import com.rushcrew.queue.application.dto.PageQuery;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.presentation.dto.request.CreatePolicyRequest;
import com.rushcrew.queue.presentation.dto.response.CreatePolicyResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * MASTER 권한만 가능
     * 타임딜 정책 목록 조회 : 상품별, 상태별(RUNNING, PAUSED, STOPPED)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Void>> getTimeDealPolicies(
        @RequestParam(required = false) UUID productId,
        @RequestParam(required = false) String status,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy,
        @RequestParam(name = "sort-direction", defaultValue = "DESC") Sort.Direction sortDirection
    ) {

        PageQuery pageQuery = PageQuery.of(page, size, sortBy, sortDirection);
        SearchPolicyCommand command = SearchPolicyCommand.of(productId, QueuePolicyStatus.valueOf(status));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("TODO"));
    }






}
