package com.star.aries.auth.common.enums;

public enum ClientStatus {

    UNKNOWN_CODE(-1, "未知类型"),
    DEVELOP(1, "开发中"),
    COMMIT(2, "版本提交"),
    AUDITING(3, "审核中"),
    UNPUBLISH(4, "未发布"),
    PUBLISH(5, "发布");

    private final int code;
    private final String desc;

    ClientStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ClientStatus fromCode(Integer code) {
        for (ClientStatus v : ClientStatus.values()) {
            if (v.code == code) {
                return v;
            }
        }
        return UNKNOWN_CODE;
    }

    public int getCode() {
        return code;
    }
}
