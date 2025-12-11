CREATE SCHEMA IF NOT EXISTS queue_schema;

/**
  ProductId UUID가 상품 서비스 DB에 없으니, 해당 sql은
  참고만 해주시고, 실제 시연이나 실행 전에 실제 productId로 QueuePolicy를 등록해주시면 됩니다.
 */
-- p_queue_policy 더미 데이터 5개 (PostgreSQL)
INSERT INTO queue_schema.p_queue_policy (
    product_id, name, status,
    start_time, end_time,
    limit_size, queue_gap, ttl,
    created_by
) VALUES
      (
          -- 1. READY 상태 (아직 시작 전)
          'b4c65e8a-09d4-4f8e-a2c6-3d7f1e5b9a01', '블랙프라이데이 한정 특가딜', 'READY',
          '2026-01-01 10:00:00', '2026-01-01 12:00:00',
          10000, 500, 300,
          1001
      ),
      (
          -- 2. RUNNING 상태 (진행 중)
          'a3c65e8a-09d4-4f8e-a2c6-3d7f1e5b9a03', '크리스마스 특별 에디션 롤백', 'RUNNING',
          '2025-12-05 14:30:00', '2025-12-05 15:00:00',
          5000, 300, 180,
          1002
      ),
      (
          -- 3. PAUSED 상태 (일시 중지)
          '550e8400-e29b-41d4-a716-446655440001', '연말정산 필수템 72시간 딜', 'PAUSED',
          '2025-12-29 09:00:00', '2026-01-01 09:00:00',
          20000, 1000, 600,
          1003
      ),
      (
          -- 4. ENDED 상태 (종료됨)
          'b9004c7a-ca6e-49dc-9931-a6b4c368bb4d', '여름 휴가 항공권 핫딜', 'ENDED',
          '2025-07-15 11:00:00', '2025-07-15 13:00:00',
          800, 100, 120,
          1004
      ),
      (
          -- 5. RUNNING 상태 (TTL 값이 높은 장시간 딜)
          '3537e8b4-0e63-481a-b4ba-605e73180717', '새해 맞이 럭키박스 이벤트', 'RUNNING',
          '2026-01-01 00:00:00', '2026-01-07 23:59:59',
          3000, 600, 1800,
          1005
      );