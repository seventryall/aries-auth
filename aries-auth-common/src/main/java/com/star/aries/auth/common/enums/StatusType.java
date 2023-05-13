package com.star.aries.auth.common.enums;


public enum StatusType {
    OK(0, "OK"),

    //5000开始，各种普通错误
    FAIL(5000, "未知错误"),    //未知类型错误
    ILLEGAL_ARGUMENT(5001, "非法参数"),
    FEIGN_ERROR(5002, "客户端调用未知异常"),

    //5100开头，数据库方面错误
    DB_ERROR(5100, "数据库操作错误"),
    DUPLICATE_COLUMN(5101, "非唯一性错误"),

    //5200开头，权限问题
//    ROLE_COUNT_LIMIT(5208,"创建角色数量不能超过10个"),
//    SEND_VERIFICATION_CODE_FAIL(5210, "验证码发送失败"),
//    VERIFICATION_CODE_ERROR(5211, "验证码错误"),
//    VERIFICATION_CODE_EXPIRATION(5214, "验证码过期"),
//    DEVICE_BINDING(5215, "您已绑定其它设备，请先解绑原设备"),
    AUTH_ERROR(5200, "认证/授权错误"),
    NO_LOGIN(5201, "访问受限，请先登录"),
    LOGIN_ERROR(5202, "账号或者密码错误"),
    TOKEN_ERROR(5203, "获取token错误"),
    INVALID_TOKEN(5204, "无效的token"),
    RESOURCE_ACCESS_DENIED(5205, "资源访问受限，请开通该资源授权"),
    INTERFACE_ACCESS_DENIED(5206, "接口访问受限，没有该接口权限"),
    LACK_PHONE_OF_USER(5207, "访问受限，请完善用户信息，填写手机号码"),
    DUPLICATE_CREATE_COMPANY(5208, "不允许重复创建公司"),
    NO_ENOUGH_CLEARANCE(5209, "访问受限，没有足够的权限"),
    NO_COMPANY(5210, "访问受限，请先创建或者加入公司"),
    NO_REGISTER(5211, "请先注册"),
    NO_CLIENT(5213, "客户端不存在"),

    INVALID_REQUEST(5400, "无效的请求");
    private final int code;
    private final String statusText;

    StatusType(int code, String statusText) {
        this.code = code;
        this.statusText = statusText;
    }

    public int getCode() {
        return code;
    }

    public String getStatusText() {
        return statusText;
    }
}
