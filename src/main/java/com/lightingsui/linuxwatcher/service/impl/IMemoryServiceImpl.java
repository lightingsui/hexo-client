package com.lightingsui.linuxwatcher.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.common.ErrorResponseCode;
import com.lightingsui.linuxwatcher.mapper.MemoryMessageMapper;
import com.lightingsui.linuxwatcher.mapper.ServerMessageMapper;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.pojo.MemoryMessage;
import com.lightingsui.linuxwatcher.service.IMemoryService;
import com.lightingsui.linuxwatcher.vo.MemoryMessageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author ：隋亮亮
 * @since ：2020/10/6 17:36
 */
@Service
public class IMemoryServiceImpl implements IMemoryService {
    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private MemoryMessageMapper memoryMessageMapper;

    public static final int BEGIN_DATE_EQUALS_END_DATE = 0;

    @Override
    public CommonResult<List<MemoryMessageVo>> getRecentlyMemoryMessage(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }
        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<MemoryMessage> memoryMessages = memoryMessageMapper.selectRecentlyMessage(serverId, INetworkServiceImpl.RECENTLY_TIME);

        List<MemoryMessageVo> res = new ArrayList<>(memoryMessages.size());

        memoryMessages.forEach(ele -> {
            MemoryMessageVo memoryMessageVo = new MemoryMessageVo();
            memoryMessageVo.setMemoryUsed(ele.getMemoryUsed());
            memoryMessageVo.setMemoryUsable(ele.getMemoryUsable());
            memoryMessageVo.setTime(ele.getMemoryTime());

            res.add(memoryMessageVo);
        });

        Collections.sort(res);


        return CommonResult.getSuccessInstance(res);
    }

    @Override
    public CommonResult<String> getMemoryStartTime(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        Date date = memoryMessageMapper.selectMemoryStartTime(serverId);

        if(date == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.CURRENT_SERVER_NOT_HAVE_START_MESSAGE);
        }

        return CommonResult.getSuccessInstance(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public CommonResult<List<MemoryMessageVo>> getAssignDateMemoryMessage(String beginDate, String endDate, ServerMessage connect) {
        // 登录验证
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        // 必备参数验证
        if(StringUtils.isBlank(beginDate) || StringUtils.isBlank(endDate)) {
            return CommonResult.getErrorInstance(ErrorResponseCode.ERROR_BEGIN_END_DATE);
        }

        // 时间校验
        DateTime parseBeginDate = DateUtil.parse(beginDate, "yyyy-MM-dd HH:mm:ss");
        DateTime parseEndDate = DateUtil.parse(endDate, "yyyy-MM-dd HH:mm:ss");

        if(DateUtil.compare(parseBeginDate, parseEndDate) > BEGIN_DATE_EQUALS_END_DATE) {
            return CommonResult.getErrorInstance(ErrorResponseCode.BEGIN_MUST_LT_END);
        }

        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<MemoryMessage> memoryMessages = memoryMessageMapper.selectAssignMessage(serverId, beginDate, endDate);

        List<MemoryMessageVo> res = new ArrayList<>(memoryMessages.size());

        memoryMessages.forEach(ele -> {
            MemoryMessageVo memoryMessageVo = new MemoryMessageVo();
            memoryMessageVo.setMemoryUsed(ele.getMemoryUsed());
            memoryMessageVo.setMemoryUsable(ele.getMemoryUsable());
            memoryMessageVo.setTime(ele.getMemoryTime());

            res.add(memoryMessageVo);
        });

        Collections.sort(res);

        return CommonResult.getSuccessInstance(res);
    }
}
