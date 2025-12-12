package com.rushcrew.timedeal.domain.vo;


public enum TimeDealProductStatus {
    IN_STOCK("재고 있음"),
    OUT_OF_STOCK("품절");

    private final String description;

    TimeDealProductStatus(String description) {
        this.description = description;
    }
}
