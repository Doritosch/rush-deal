package com.rushcrew.queue.application.dto;

import lombok.Builder;
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
}
