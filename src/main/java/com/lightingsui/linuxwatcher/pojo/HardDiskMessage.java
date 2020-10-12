package com.lightingsui.linuxwatcher.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date hardDiskTime;

    private String hardDiskUsable;
}