package com.lightingsui.linuxwatcher.service;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.vo.LoadavgMessageVo;

import java.util.List;

/**
 * 负载信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/9 7:47
 */
public interface ILoadavgMessage {
    CommonResult<String> getLoadavgStartTime(ServerMessage connect);

    CommonResult<List<LoadavgMessageVo>> getRecentlyLoadavgMessage(ServerMessage connect);

    CommonResult<List<LoadavgMessageVo>> getAssignDateLoadavgMessage(String beginDate, String endDate, ServerMessage connect);
}
