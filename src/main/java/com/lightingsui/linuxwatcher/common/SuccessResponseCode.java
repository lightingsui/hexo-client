package com.lightingsui.linuxwatcher.common;

/**
 * 成功消息
 *
 * @author ：隋亮亮
 * @since ：2020/10/4 18:33
 */
public enum SuccessResponseCode implements ISuccessCode {
    LOGIN_SUCCESS("200", "登陆成功"),
    OPERATE_SUCCESS("200", "操作成功");




    /**
     * 响应码
     */
    private String responseCode;

    /**
     * 响应信息
     */
    private String responseMessage;

    private SuccessResponseCode(String responseCode, String responseMessage) {
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
