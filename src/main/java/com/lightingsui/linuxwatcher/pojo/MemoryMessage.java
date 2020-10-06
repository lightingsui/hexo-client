package com.lightingsui.linuxwatcher.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

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
    private Date memoryTime;
}