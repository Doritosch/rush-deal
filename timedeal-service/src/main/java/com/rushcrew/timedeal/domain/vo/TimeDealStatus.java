package com.rushcrew.timedeal.domain.vo;

public enum TimeDealStatus {
    SCHEDULED("예정"),
    IN_PROGRESS("진행중"),
    SOLD_OUT("품절"),
    ENDED("종료");

    private final String description;

    TimeDealStatus(String description) {
        this.description = description;
    }
}
