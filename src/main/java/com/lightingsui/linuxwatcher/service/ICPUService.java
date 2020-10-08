package com.lightingsui.linuxwatcher.service;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.vo.CPUMessageVo;

import java.util.List;

/**
 * cpu 信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/8 17:03
 */
public interface ICPUService {
    CommonResult<String> getCpuStartTime(ServerMessage connect);

    CommonResult<List<CPUMessageVo>> getRecentlyCPUMessage(ServerMessage connect);

    CommonResult<List<CPUMessageVo>> getAssignDateCPUMessage(String beginDate, String endDate, ServerMessage connect);
}
