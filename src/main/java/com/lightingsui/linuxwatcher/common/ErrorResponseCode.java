package com.lightingsui.linuxwatcher.common;

/**
 * 成功消息
 *
 * @author ：隋亮亮
 * @since ：2020/10/4 18:33
 */
public enum ErrorResponseCode implements IErrorCode {
    NORMAL_ERROR_CODE("500", "服务器异常"),
    UNKNOWN_EXCEPTION("501", "未知的异常"),
    NESTED_MESSAGE_NOT_SUPPORT("500", "至少提供端口和密码"),
    UN_ROOT_USER("500", "非root用户，请使用root用户登录"),
    LOGIN_NORMAL_ERROR("500", "连接异常，请重新尝试"),
    LOGIN_FAILED("500", "登录失败，请检查输入信息"),
    LOGIN_FAILED_UPDATE("510", "登录后才能访问此接口"),
    DATABASE_ERROR("500", "数据库服务器异常"),
    UNAME_GET_ERROR("500", "服务器信息获取失败"),
    IP_TO_LOCATION_FAILED("500", "ip转换地址错误"),
    NOT_INSTALL_DSTAT("500", "没有安装dstat"),



    CURRENT_SERVER_NOT_HAVE_START_MESSAGE("500", "当前服务器没有第一次启动信息"),
    ERROR_BEGIN_END_DATE("500", "请提供开始时间和结束时间"),
    BEGIN_MUST_LT_END("500", "开始时间必须小于结束时间"),
    ;




    /**
     * 响应码
     */
    private String responseCode;

    /**
     * 响应信息
     */
    private String responseMessage;

    private ErrorResponseCode(String responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    @Override
    public String getResponseCode() {
        return this.responseCode;
    }

    @Override
    public String getResponseMessage() {
        return this.responseMessage;
    }
}
