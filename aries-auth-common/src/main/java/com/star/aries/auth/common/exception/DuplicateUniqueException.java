package com.star.aries.auth.common.exception;


public class DuplicateUniqueException extends RuntimeException {

    private final String objectJsonString;

    public DuplicateUniqueException(String objectJsonString, String message) {
        super(message);
        this.objectJsonString = objectJsonString;
    }

    public String getObjectJsonString() {
        return objectJsonString;
    }


}
