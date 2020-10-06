package com.lightingsui.linuxwatcher.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

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
    private Date blogTime;

    @ApiModelProperty("博客名称")
    private String blogName;

    @ApiModelProperty("博客分类")
    private String blogCategory;

    @ApiModelProperty("博客标签")
    private String blogTag;
}