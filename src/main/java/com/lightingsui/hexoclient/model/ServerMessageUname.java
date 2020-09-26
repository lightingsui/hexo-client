package com.lightingsui.hexoclient.model;

import lombok.Data;

/**
 * 服务器详情信息
 *
 * @author ：隋亮亮
 * @since ：2020/9/23 14:52
 */
@Data
public class ServerMessageUname {
    private String kernelName;
    private String nodeName;
    private String kernelRelease;
    private String machine;
    private String processor;
    private String operatingSystem;
    private String hardwarePlatform;
    private String kernelVersion;
}
