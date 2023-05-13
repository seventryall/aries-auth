package com.star.aries.auth.common.exception;


import com.star.aries.auth.common.enums.StatusType;

public class CommonRuntimeException extends RuntimeException {

    private final StatusType statusType;

    public CommonRuntimeException(StatusType statusType, String message) {
        super(message);
        this.statusType = statusType;
    }

    public StatusType getStatusType() {
        return this.statusType;
    }
}
