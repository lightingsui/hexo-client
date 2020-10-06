package com.lightingsui.linuxwatcher.service;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.vo.MemoryMessageVo;

import java.util.List;

/**
 * 内存信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/6 17:35
 */
public interface IMemoryService {
    CommonResult<List<MemoryMessageVo>> getRecentlyMemoryMessage(ServerMessage connect);

    CommonResult<String> getMemoryStartTime(ServerMessage connect);

    CommonResult<List<MemoryMessageVo>> getAssignDateMemoryMessage(String beginDate, String endDate, ServerMessage connect);
}
