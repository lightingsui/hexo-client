package com.lightingsui.linuxwatcher.vo;

import cn.hutool.core.date.DateUtil;
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
public class HardDiskMessageVo implements Comparable<HardDiskMessageVo> {
    private String used;
    private String usabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GTM+8")
    private Date time;

    @Override
    public int compareTo(HardDiskMessageVo o) {
        return DateUtil.compare(this.time, o.getTime());
    }
}
