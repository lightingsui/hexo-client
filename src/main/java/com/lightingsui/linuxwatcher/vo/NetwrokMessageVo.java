package com.lightingsui.linuxwatcher.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 最近网络信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/6 16:15
 */
@Data
public class NetwrokMessageVo {
    private String inSpeed;
    private String outSpeed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;
}
