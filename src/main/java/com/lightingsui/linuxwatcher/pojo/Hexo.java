package com.lightingsui.linuxwatcher.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Table Name: hexo
 */
@Data
public class Hexo {
    @ApiModelProperty("主键")
    private Integer hexoId;

    @ApiModelProperty("服务器信息外键")
    private Integer serverId;

    @ApiModelProperty("hexo新建模板")
    private String hexoTemplate;
}