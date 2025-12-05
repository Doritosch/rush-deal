package com.rushcrew.auth_service.auth.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenId {

    private final String value;

    public static TokenId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Token ID는 필수입니다.");
        }
        return new TokenId(value);
    }

    @JsonCreator
    public static TokenId fromJson(@JsonProperty("value") String value) {
        return of(value);
    }
}
