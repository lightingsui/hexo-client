package com.lightingsui.linuxwatcher.service;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.vo.NetwrokMessageVo;

import java.util.List;

/**
 * 网络信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/5 16:41
 */
public interface INetworkService {
    CommonResult<Boolean> checkInstallDstat(ServerMessage connect);

    CommonResult<String> getNetworkStartTime(ServerMessage connect);

    CommonResult<List<NetwrokMessageVo>> getRecentlyNetworkMessage(ServerMessage connect);

    CommonResult<List<NetwrokMessageVo>> getAssignDateNetworkMessage(String beginDate, String endDate, ServerMessage connect);
}
