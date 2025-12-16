package com.rushcrew.payment_service.presentation;

import com.rushcrew.payment_service.application.PaymentService;
import com.rushcrew.payment_service.application.command.PaymentCommand;
import com.rushcrew.payment_service.application.result.PaymentPrepareResult;
import com.rushcrew.payment_service.application.result.PaymentResult;
import com.rushcrew.payment_service.presentation.dto.request.CancelPaymentRequest;
import com.rushcrew.payment_service.presentation.dto.request.CompletePaymentRequest;
import com.rushcrew.payment_service.presentation.dto.request.PaymentRequest;
import com.rushcrew.payment_service.presentation.dto.response.PaymentPrepareResponse;
import com.rushcrew.payment_service.presentation.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import kotlin.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 준비 - Payment 엔티티를 먼저 생성하고 PortOne paymentId를 반환
     * @param request itemId, amount, currency, orderName
     * @return paymentId (우리 시스템의 Payment ID), portOnePaymentId (PortOne에서 사용할 ID)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'MASTER')")
    public ResponseEntity<PaymentPrepareResponse> preparePayment(
            @Valid @RequestBody PaymentRequest request
    ) {
        PaymentCommand command = request.toCommand();

        PaymentPrepareResult result = paymentService.preparePayment(command);

        return ResponseEntity.ok(PaymentPrepareResponse.from(result));
    }

    // 인증 결제(결제창을 이용한 결제)를 위한 엔드포인트입니다.
    //
    // 브라우저에서 결제 완료 후 서버에 결제 완료를 알리는 용도입니다.
    // 결제 수단 및 PG사 사정에 따라 결제 완료 후 승인이 지연될 수 있으므로
    // 결제 정보를 완전히 실시간으로 얻기 위해서는 웹훅을 사용해야 합니다.
    //
    // 인증 결제 연동 가이드: https://developers.portone.io/docs/ko/authpay/guide?v=v2
    @PostMapping("/complete")
    public Mono<PaymentResponse> completePayment(
            @RequestBody CompletePaymentRequest completeRequest
    ) {
        return paymentService.completePayment(
                completeRequest.paymentId(),
                completeRequest.portOnePaymentId()
        );
    }

    @PostMapping("/{paymentId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'MASTER')")
    public Mono<PaymentResponse> cancelPayment(
            @PathVariable("paymentId") UUID paymentId,
            @Valid @RequestBody CancelPaymentRequest request
            ) {

        Mono<PaymentResult> result = paymentService.cancelPayment(paymentId, request.cancelReason());
        return result.map(PaymentResponse::from);
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'MASTER')")
    public ResponseEntity<PaymentResponse> getPaymentByPaymentId(@PathVariable("paymentId") UUID paymentId) {
        PaymentResult result = paymentService.findPaymentByPaymentId(paymentId);

        return ResponseEntity.ok(PaymentResponse.from(result));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'MASTER')")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable("orderId") UUID orderId) {
        PaymentResult result = paymentService.findPaymentByOrderId(orderId);

        return ResponseEntity.ok(PaymentResponse.from(result));
    }

    // 결제 정보를 실시간으로 전달받기 위한 웹훅입니다.
    // 관리자 콘솔에서 웹훅 정보를 등록해야 사용할 수 있습니다.
    //
    // 웹훅 연동 가이드: https://developers.portone.io/docs/ko/v2-payment/webhook?v=v2
    // TODO webhook 연동
    @PostMapping("/webhook")
    public Mono<Unit> handleWebhook(
            @RequestBody String body,
            @RequestHeader("webhook-id") String webhookId,
            @RequestHeader("webhook-timestamp") String webhookTimestamp,
            @RequestHeader("webhook-signature") String webhookSignature
    ) throws Exception {
        return paymentService.handleWebhook(body, webhookId, webhookTimestamp, webhookSignature);
    }

}
