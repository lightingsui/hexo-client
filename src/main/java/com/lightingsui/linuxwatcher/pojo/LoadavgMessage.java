package com.lightingsui.linuxwatcher.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Table Name: loadavg_message
 */
@Data
public class LoadavgMessage {
    @ApiModelProperty("主键")
    private Integer loadavgId;

    @ApiModelProperty("服务器id")
    private Integer serverId;

    @ApiModelProperty("当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date loadavgTime;

    @ApiModelProperty("1分钟平均负载")
    private String loadavgOne;

    @ApiModelProperty("5分钟负载")
    private String loadavgFive;

    @ApiModelProperty("15分钟负载")
    private String loadavgFifteen;
}