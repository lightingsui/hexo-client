package com.lightingsui.linuxwatcher.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.common.ErrorResponseCode;
import com.lightingsui.linuxwatcher.mapper.LoadavgMessageMapper;
import com.lightingsui.linuxwatcher.mapper.ServerMessageMapper;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.pojo.LoadavgMessage;
import com.lightingsui.linuxwatcher.service.ILoadavgMessage;
import com.lightingsui.linuxwatcher.vo.LoadavgMessageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author ：隋亮亮
 * @since ：2020/10/9 7:48
 */
@Service
public class ILoadavgMessageImpl implements ILoadavgMessage {
    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private LoadavgMessageMapper loadavgMessageMapper;

    @Override
    public CommonResult<String> getLoadavgStartTime(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        Date date = loadavgMessageMapper.selectLoadavgStartTime(serverId);

        if(date == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.CURRENT_SERVER_NOT_HAVE_START_MESSAGE);
        }

        return CommonResult.getSuccessInstance(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public CommonResult<List<LoadavgMessageVo>> getRecentlyLoadavgMessage(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }
        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<LoadavgMessage> loadavgMessages = loadavgMessageMapper.selectRecentlyMessage(serverId, INetworkServiceImpl.RECENTLY_TIME);

        List<LoadavgMessageVo> res = new ArrayList<>(loadavgMessages.size());

        loadavgMessages.forEach(ele -> {
            LoadavgMessageVo loadavgMessageVo = new LoadavgMessageVo();
            loadavgMessageVo.setLoadavgOne(ele.getLoadavgOne());
            loadavgMessageVo.setLoadavgFive(ele.getLoadavgFive());
            loadavgMessageVo.setLoadavgFifteen(ele.getLoadavgFifteen());
            loadavgMessageVo.setTime(ele.getLoadavgTime());

            res.add(loadavgMessageVo);
        });

        Collections.sort(res);

        return CommonResult.getSuccessInstance(res);
    }

    @Override
    public CommonResult<List<LoadavgMessageVo>> getAssignDateLoadavgMessage(String beginDate, String endDate, ServerMessage connect) {
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

        if(DateUtil.compare(parseBeginDate, parseEndDate) > IMemoryServiceImpl.BEGIN_DATE_EQUALS_END_DATE) {
            // 开始时间必须小于结束时间
            return CommonResult.getErrorInstance(ErrorResponseCode.BEGIN_MUST_LT_END);
        }

        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<LoadavgMessage> loadavgMessages = loadavgMessageMapper.selectAssignMessage(serverId, beginDate, endDate);

        List<LoadavgMessageVo> res = new ArrayList<>(loadavgMessages.size());

        loadavgMessages.forEach(ele -> {
            LoadavgMessageVo loadavgMessageVo = new LoadavgMessageVo();
            loadavgMessageVo.setLoadavgOne(ele.getLoadavgOne());
            loadavgMessageVo.setLoadavgFive(ele.getLoadavgFive());
            loadavgMessageVo.setLoadavgFifteen(ele.getLoadavgFifteen());
            loadavgMessageVo.setTime(ele.getLoadavgTime());

            res.add(loadavgMessageVo);
        });

        Collections.sort(res);

        return CommonResult.getSuccessInstance(res);
    }
}
