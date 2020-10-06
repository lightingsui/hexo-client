package com.lightingsui.linuxwatcher.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * Table Name: cpu_message
 */
@Data
public class CpuMessage {
    @ApiModelProperty("cpu使用情况id")
    private Integer cpuId;

    @ApiModelProperty("服务器id")
    private Integer serverId;

    @ApiModelProperty("cpu使用量")
    private String cpuUsed;

    @ApiModelProperty("当前时间")
    private Date cpuTime;
}