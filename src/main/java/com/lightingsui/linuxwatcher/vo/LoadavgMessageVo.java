package com.lightingsui.linuxwatcher.vo;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 负载信息vo
 *
 * @author ：隋亮亮
 * @since ：2020/10/9 7:50
 */
@Data
public class LoadavgMessageVo implements Comparable<LoadavgMessageVo> {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;
    private String loadavgOne;
    private String loadavgFive;
    private String loadavgFifteen;

    @Override
    public int compareTo(LoadavgMessageVo o) {
        return DateUtil.compare(this.time, o.getTime());
    }
}
