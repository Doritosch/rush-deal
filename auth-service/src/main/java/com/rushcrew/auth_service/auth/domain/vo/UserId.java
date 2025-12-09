package com.rushcrew.auth_service.auth.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {

    private final Long value;

    public static UserId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("User ID는 양수여야 합니다.");
        }
        return new UserId(value);
    }

    @JsonCreator
    public static UserId fromJson(@JsonProperty("value") Long value) {
        return of(value);
    }
}
