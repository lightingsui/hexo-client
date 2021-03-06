package com.lightingsui.linuxwatcher.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Table Name: memory_message
 */
@Data
public class MemoryMessage {
    @ApiModelProperty("内存信息id")
    private Integer memoryId;

    @ApiModelProperty("服务器id")
    private Integer serverId;

    @ApiModelProperty("内存使用情况")
    private String memoryUsed;

    @ApiModelProperty("当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date memoryTime;

    @ApiModelProperty("内存可用空间")
    private String memoryUsable;
}