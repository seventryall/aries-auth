package com.star.aries.auth.common.util;


import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.pojo.MsgResult;

public class MsgResultUtil {

    public static <T> MsgResult buildSuccess(T t) {
        return MsgResult.builder()
                .status(StatusType.OK.getCode())
                .statusText(StatusType.OK.getStatusText())
                .data(t)
                .build();
    }

    public static MsgResult buildFail() {
        return MsgResult.builder()
                .status(StatusType.FAIL.getCode())
                .statusText(StatusType.FAIL.getStatusText())
                .build();
    }
}
