package com.lightingsui.linuxwatcher.vo;

import lombok.Data;

/**
 * 服务器信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/4 19:56
 */
@Data
public class ServerMessageVo {
    /** 内存信息 */
    private String memoryTotal;
    private String memorySwapTotal;
    private String memoryAndSwapTotal;

    /** 硬盘信息 */
    private String hardDiskTotal;

    /** 系统启动时间，运行时间 */
    private String runTime;
    private String startUpTime;

    /** 系统用户 */
    private Integer totalUserNum;
    private Integer currentOnLineUserNum;

    /** 进程信息 */
    private Integer totalProcess;
    private Integer runningProcess;
    private Integer sleepingProcess;
    private Integer stoppedProcess;
    private Integer zombieProcess;
}
