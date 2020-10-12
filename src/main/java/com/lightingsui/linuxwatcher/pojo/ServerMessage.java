package com.lightingsui.linuxwatcher.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Table Name: server_message
 */
@Data
public class ServerMessage {
    @ApiModelProperty("服务器id")
    private Integer serverId;

    @ApiModelProperty("服务器ip > 最大长度150")
    private String serverIp;

    @ApiModelProperty("服务器主机名")
    private String serverName;

    @ApiModelProperty("服务器创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String serverCreateTime;

    @ApiModelProperty("上次登录时间")
    private Date serverLastTime;

    @ApiModelProperty("上次登录地点")
    private String serverLoginLocation;

    @ApiModelProperty("hexo博客地址")
    private String serverHexoAddress;

    @ApiModelProperty("服务器操作系统")
    private String serverOperatingSystem;

    @ApiModelProperty("服务器密码")
    private String serverPassword;

    @ApiModelProperty("端口")
    private String serverPort;
}