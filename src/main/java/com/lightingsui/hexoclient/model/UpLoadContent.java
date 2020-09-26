package com.lightingsui.hexoclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 上传接收内容
 *
 * @author ：隋亮亮
 * @since ：2020/9/25 8:17
 */
@Data
public class UpLoadContent {
    @JsonProperty("content")
    private String content;
    @JsonProperty("fileName")
    private String fileName;
}
