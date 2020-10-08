package com.lightingsui.linuxwatcher.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * cpu vo
 *
 * @author ：隋亮亮
 * @since ：2020/10/8 17:06
 */
@Data
public class CPUMessageVo {
    @ApiModelProperty("cpu使用量")
    private String cpuUsed;

    @ApiModelProperty("当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cpuTime;

    @ApiModelProperty("用户空间占用CPU百分比")
    private String cpuUs;

    @ApiModelProperty("内核空间占用CPU百分比")
    private String cpuSy;

    @ApiModelProperty("用户进程空间内改变过优先级的进程占用CPU百分比")
    private String cpuNi;

    @ApiModelProperty("等待输入输出的CPU时间百分比")
    private String cpuWa;

    @ApiModelProperty("硬件CPU中断占用百分比")
    private String cpuHi;

    @ApiModelProperty("软中断占用百分比")
    private String cpuSi;

    @ApiModelProperty("虚拟机占用百分比")
    private String cpuSt;
}
