package com.star.aries.auth.server.endpoint;

import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.exception.CommonRuntimeException;
import com.star.aries.auth.common.exception.DuplicateUniqueException;
import com.star.aries.auth.common.pojo.MsgResult;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionEndpointAdvice {

    @ExceptionHandler(value = NoSuchClientException.class)
    public MsgResult<Object> noClientHandler(InvalidGrantException ex) {
        ex.printStackTrace();
        return MsgResult.builder().status(StatusType.NO_CLIENT.getCode()).statusText(StatusType.NO_CLIENT.getStatusText()).build();
    }

    @ExceptionHandler(value = InvalidGrantException.class)
    public MsgResult<Object> invalidGrandHandler(InvalidGrantException ex) {
        ex.printStackTrace();
        return MsgResult.builder().status(StatusType.LOGIN_ERROR.getCode()).statusText(StatusType.LOGIN_ERROR.getStatusText()).build();
    }

    @ExceptionHandler(value = OAuth2Exception.class)
    public MsgResult<Object> oauth2Handler(OAuth2Exception ex) {
        ex.printStackTrace();
        return MsgResult.builder().status(StatusType.AUTH_ERROR.getCode()).statusText(StatusType.AUTH_ERROR.getStatusText()).build();
    }

    @ExceptionHandler(value = DuplicateUniqueException.class)
    public MsgResult<Object> duplicateUniqueHandler(DuplicateUniqueException ex) {
        ex.printStackTrace();
        return MsgResult.builder().status(StatusType.DUPLICATE_COLUMN.getCode()).statusText(ex.getMessage()).build();
    }

    @ExceptionHandler(value = CommonRuntimeException.class)
    public MsgResult<Object> commonRuntimeHandler(CommonRuntimeException ex) {
        ex.printStackTrace();
        return MsgResult.builder().status(ex.getStatusType().getCode()).statusText(ex.getMessage()).build();
    }

    @ExceptionHandler(value = RuntimeException.class)
    public MsgResult<Object> runtimeHandler(RuntimeException ex) {
        ex.printStackTrace();
        return MsgResult.builder().status(StatusType.FAIL.getCode()).statusText(StatusType.FAIL.getStatusText()).build();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public MsgResult<Object> illegalArgumentHandler(IllegalArgumentException ex) {
        return MsgResult.builder().status(StatusType.ILLEGAL_ARGUMENT.getCode()).statusText(ex.getMessage()).build();
    }
}