package com.lightingsui.linuxwatcher.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Table Name: network_message
 */
@Data
public class NetworkMessage {
    @ApiModelProperty("网络速度id")
    private Integer networkId;

    @ApiModelProperty("服务器id")
    private Integer serverId;

    @ApiModelProperty("出网速度")
    private String networkOutSpeed;

    @ApiModelProperty("入网速度")
    private String networkInSpeed;

    @ApiModelProperty("当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date networkTime;

    @ApiModelProperty("最大出网速度")
    private String networkOutMaxSpeed;

    @ApiModelProperty("最大入网速度")
    private String networkInMaxSpeed;

    @ApiModelProperty("平均入网速度")
    private String networkInAvgSpeed;

    @ApiModelProperty("平均出网速度")
    private String networkOutAvgSpeed;
}