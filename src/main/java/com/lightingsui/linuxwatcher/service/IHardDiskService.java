package com.lightingsui.linuxwatcher.service;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.vo.HardDiskMessageVo;

import java.util.List;

/**
 * 硬盘信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/7 8:51
 */
public interface IHardDiskService {
    CommonResult<String> getHardDiskStartTime(ServerMessage connect);

    CommonResult<List<HardDiskMessageVo>> getRecentlyHardDiskMessage(ServerMessage connect);

    CommonResult<List<HardDiskMessageVo>> getAssignDateHardDiskMessage(String beginDate, String endDate, ServerMessage connect);
}
