package com.lightingsui.linuxwatcher.vo;

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
public class LastLoginMessageVo {
    @JsonFormat(pattern = "yyyy年MM月dd日 HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginDate;
    private String lastLoginLocation;
}
