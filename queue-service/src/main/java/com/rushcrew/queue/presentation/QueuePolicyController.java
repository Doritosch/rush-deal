package com.rushcrew.queue.presentation;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.application.command.SearchPolicyCommand;
import com.rushcrew.queue.application.command.UpdatePolicyCommand;
import com.rushcrew.queue.application.dto.PageQuery;
import com.rushcrew.queue.application.dto.QueuePolicyQueryResponse;
import com.rushcrew.queue.application.service.QueuePolicyService;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.presentation.dto.request.CreatePolicyRequest;
import com.rushcrew.queue.presentation.dto.request.UpdatePolicyRequest;
import com.rushcrew.queue.presentation.dto.response.CreatePolicyResponse;
import com.rushcrew.queue.presentation.dto.response.PagedPolicyResponse;
import com.rushcrew.queue.presentation.dto.response.QueuePolicyResponse;
import com.rushcrew.queue.presentation.mapper.QueuePolicyPresentationMapper;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/queue/policies")
public class QueuePolicyController {

    private final QueuePolicyService queuePolicyService;
    private final QueuePolicyPresentationMapper presentationMapper;

    // API Gateway에서 인증 후, USER ID와 ROLE을 헤더에 담아 전달
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    public QueuePolicyController(QueuePolicyService queuePolicyService,
        QueuePolicyPresentationMapper presentationMapper) {
        this.queuePolicyService = queuePolicyService;
        this.presentationMapper = presentationMapper;
    }

    /**
     * 타임딜 정책 생성 : MASTER 권한만 가능
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePolicyResponse>> registerPolicy(
        @Valid @RequestBody CreatePolicyRequest request,
        @RequestHeader(USER_ID_HEADER) Long currUserId,
        @RequestHeader(USER_ROLE_HEADER) String role
    ) {
        CreatePolicyCommand command = request.toCommand();
        QueuePolicyQueryResponse result = queuePolicyService.createQueuePolicy(command, currUserId, role);
        CreatePolicyResponse response = presentationMapper.toCreatePolicyResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * MASTER 권한만 가능
     * 타임딜 정책 목록 조회 : 상품별, 상태별(RUNNING, PAUSED, STOPPED)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedPolicyResponse>> getTimeDealPolicies(
        @RequestParam(required = false) UUID productId,
        @RequestParam(required = false) String status,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy,
        @RequestParam(name = "sort-direction", defaultValue = "DESC") Sort.Direction sortDirection,
        @RequestHeader(USER_ID_HEADER) Long currUserId,
        @RequestHeader(USER_ROLE_HEADER) String role
    ) {

        PageQuery pageQuery = PageQuery.of(page, size, sortBy, sortDirection);
        SearchPolicyCommand command = SearchPolicyCommand.of(productId, status);
        Page<QueuePolicyQueryResponse> pageResult = queuePolicyService.searchPolicies(
            pageQuery, command, currUserId, role);
        PagedPolicyResponse response = presentationMapper.toPagedPolicyResponse(pageResult);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    /**
     * MASTER 권한만 가능
     * 타임딜 정책 정보 조회 (단건)
     */
    @GetMapping("/{policy-id}")
    public ResponseEntity<ApiResponse<QueuePolicyResponse>> getPolicyInfo(
        @PathVariable("policy-id") UUID policyId,
        @RequestHeader(USER_ID_HEADER) Long currUserId,
        @RequestHeader(USER_ROLE_HEADER) String role
    ) {
        QueuePolicyQueryResponse result = queuePolicyService.getQueuePolicyInfo(policyId, currUserId, role);
        QueuePolicyResponse response = presentationMapper.toQueuePolicyResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    /**
     * MASTER 권한만 가능
     * 타임딜 정책 수정
     */
    @PatchMapping("/{policy-id}")
    public ResponseEntity<ApiResponse<QueuePolicyResponse>> updatePolicy(
        @PathVariable("policy-id") UUID policyId,
        @RequestBody UpdatePolicyRequest request,
        @RequestHeader(USER_ID_HEADER) Long currUserId,
        @RequestHeader(USER_ROLE_HEADER) String role
    ) {
        UpdatePolicyCommand command = request.toCommand();
        QueuePolicyQueryResponse result = queuePolicyService.updateQueuePolicy(command, policyId,
            currUserId, role);
        QueuePolicyResponse response = presentationMapper.toQueuePolicyResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    /**
     * 타임딜 정책 삭제
     */
    @DeleteMapping("/{policy-id}")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(
        @PathVariable("policy-id") UUID policyId,
        @RequestHeader(USER_ID_HEADER) Long currUserId,
        @RequestHeader(USER_ROLE_HEADER) String role
    ) {
        queuePolicyService.deleteQueuePolicy(policyId, currUserId, role);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }
}
