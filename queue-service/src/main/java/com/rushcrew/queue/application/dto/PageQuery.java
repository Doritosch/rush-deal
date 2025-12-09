package com.rushcrew.queue.application.dto;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Builder
public record PageQuery(
    Integer page,
    Integer size,
    String sortBy, // 정렬 기준 필드 (예: "createdAt")
    Sort.Direction direction
) {

    public static PageQuery of(Integer page, Integer size, String sortBy, Sort.Direction direction) {
        return new PageQuery(page, size, sortBy, direction);
    }

    public PageRequest toPageable() {
        // 쿼리에서 페이지 1이면 0, 2이면 1로 변환
        int actualPage = (page <= 0) ? 0 : page - 1;
        // 정렬 기준이 없으면 기본값으로 "createdAt"을 사용
        String actualSortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
        return PageRequest.of(actualPage, size, direction, actualSortBy);
    }
}
