package com.lightingsui.linuxwatcher.vo;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 上次登录信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/5 10:31
 */
@Data
public class LastLoginMessageVo implements Comparable<LastLoginMessageVo> {
    @JsonFormat(pattern = "yyyy年MM月dd日 HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginDate;
    private String lastLoginLocation;

    @Override
    public int compareTo(LastLoginMessageVo o) {
        return DateUtil.compare(this.lastLoginDate, o.getLastLoginDate());
    }
}
