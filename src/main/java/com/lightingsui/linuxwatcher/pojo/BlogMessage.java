package com.lightingsui.linuxwatcher.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Table Name: blog_message
 */
@Data
public class BlogMessage {
    @ApiModelProperty("博客发表id")
    private Integer blogId;

    @ApiModelProperty("服务器id")
    private Integer serverId;

    @ApiModelProperty("博客上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date blogTime;

    @ApiModelProperty("博客名称")
    private String blogName;

    @ApiModelProperty("博客分类")
    private String blogCategory;

    @ApiModelProperty("博客标签")
    private String blogTag;
}