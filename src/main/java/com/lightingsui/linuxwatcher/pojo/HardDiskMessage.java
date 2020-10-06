package com.lightingsui.linuxwatcher.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * Table Name: hard_disk_message
 */
@Data
public class HardDiskMessage {
    @ApiModelProperty("硬盘使用情况id")
    private Integer hardDiskId;

    @ApiModelProperty("服务器id")
    private Integer serverId;

    @ApiModelProperty("硬盘使用量")
    private String hardDiskUsed;

    @ApiModelProperty("当前时间")
    private Date hardDiskTime;

    private String hardDiskUsable;
}