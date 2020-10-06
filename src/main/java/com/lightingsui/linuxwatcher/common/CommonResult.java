package com.lightingsui.linuxwatcher.common;

import lombok.Data;

/**
 * 通用前台返回值
 *
 * @author ：隋亮亮
 * @since ：2020/3/6 21:49
 */
@Data
public class CommonResult<T> {
    private String responseCode;
    private String responseMessage;
    private T data;

    private CommonResult(String responseCode, String responseMessage, T data) {
        this.data = data;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    private CommonResult() {

    }

    /**
     * 自定义（成功）返回
     *
     * @param successCode 成功信息
     * @param data        返回数据
     * @param <T>         返回数据类型
     * @return 实例对象
     */
    public static <T> CommonResult<T> getSuccessInstance(ISuccessCode successCode, T data) {
        return new CommonResult<T>(successCode.getResponseCode(), successCode.getResponseMessage(), data);
    }

    /**
     * 非自定义（成功）返回消息（只传data）
     *
     * @param data 返回数据
     * @param <T>  返回数据类型
     * @return 实例对象
     */
    public static <T> CommonResult<T> getSuccessInstance(T data) {
        return getSuccessInstance(SuccessResponseCode.OPERATE_SUCCESS, data);
    }

    /**
     * 自定义（成功）响应信息
     *
     * @param responseMessage 响应信息
     * @param data            返回数据
     * @param <T>             返回数据类型
     * @return 实例对象
     */
    public static <T> CommonResult<T> getSuccessInstance(String responseMessage, T data) {
        return new CommonResult<T>(SuccessResponseCode.OPERATE_SUCCESS.getResponseCode(), responseMessage, data);
    }

    /**
     * 自定义（失败）
     *
     * @param errorCode 失败信息
     * @param <T>       返回类型
     * @return 实例对象
     */
    public static <T> CommonResult<T> getErrorInstance(IErrorCode errorCode) {
        return new CommonResult<T>(errorCode.getResponseCode(), errorCode.getResponseMessage(), null);
    }

    /**
     * 通用（失败返回）
     *
     * @param <T> 返回类型
     * @return 实例对象
     */
    public static <T> CommonResult<T> getErrorInstance() {
        return getErrorInstance(ErrorResponseCode.NORMAL_ERROR_CODE);
    }

    /**
     * 自定义（失败）信息
     *
     * @param errorMessage 失败信息
     * @param <T>          返回类型
     * @return 实例对象
     */
    public static <T> CommonResult<T> getErrorInstance(String errorMessage) {
        return new CommonResult<T>(ErrorResponseCode.NORMAL_ERROR_CODE.getResponseCode(), errorMessage, null);
    }

}
