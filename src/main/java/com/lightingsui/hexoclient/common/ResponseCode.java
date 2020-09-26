package com.lightingsui.hexoclient.common;

/**
 * 返回信息
 *
 * @author ：隋亮亮
 * @since ：2020/3/6 21:56
 */
public enum ResponseCode implements IErrorCode, ISuccessCode {
    LOGIN_SUCCESS("200", "登陆成功"),

    OPERATE_SUCCESS("200", "操作成功"),
    NORMAL_ERROR_CODE("500", "服务器异常"),
    UNKNOWN_EXCEPTION("501", "未知的异常");

    /**
     * 响应码
     */
    private String responseCode;

    /**
     * 响应信息
     */
    private String responseMessage;

    private ResponseCode(String responseCode, String responseMessage) {
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
