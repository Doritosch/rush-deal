package com.rushcrew.queue.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenId {

    private UUID value;

    public TokenId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("[Queue:Error] 토큰 ID는 비어있을 수 없습니다.");
        }
        this.value = value;
    }

    public static TokenId generate() {
        return new TokenId(UUID.randomUUID());
    }

    public static TokenId of(UUID value) {
        return new TokenId(value);
    }
}
