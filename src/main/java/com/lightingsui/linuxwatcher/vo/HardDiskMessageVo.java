package com.lightingsui.linuxwatcher.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 硬盘信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/7 8:57
 */
@Data
public class HardDiskMessageVo {
    private String used;
    private String usabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GTM+8")
    private Date time;
}
