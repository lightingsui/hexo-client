package com.lightingsui.linuxwatcher.vo;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 内存vo
 *
 * @author ：隋亮亮
 * @since ：2020/10/6 17:38
 */
@Data
public class MemoryMessageVo implements Comparable<MemoryMessageVo> {
    private String memoryUsed;
    private String memoryUsable;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    @Override
    public int compareTo(MemoryMessageVo o) {
        return DateUtil.compare(this.time, o.getTime());
    }
}
