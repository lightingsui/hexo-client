package com.lightingsui.linuxwatcher.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.common.ErrorResponseCode;
import com.lightingsui.linuxwatcher.mapper.CpuMessageMapper;
import com.lightingsui.linuxwatcher.mapper.ServerMessageMapper;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.pojo.CpuMessage;
import com.lightingsui.linuxwatcher.service.ICPUService;
import com.lightingsui.linuxwatcher.vo.CPUMessageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.lightingsui.linuxwatcher.service.impl.IMemoryServiceImpl.BEGIN_DATE_EQUALS_END_DATE;

/**
 * @author ：隋亮亮
 * @since ：2020/10/8 17:03
 */
@Service
public class ICPUServiceImpl implements ICPUService {
    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private CpuMessageMapper cpuMessageMapper;

    @Override
    public CommonResult<List<CPUMessageVo>> getRecentlyCPUMessage(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }
        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<CpuMessage> cpuMessages = cpuMessageMapper.selectRecentlyMessage(serverId, INetworkServiceImpl.RECENTLY_TIME);

        List<CPUMessageVo> res = new ArrayList<>(cpuMessages.size());

        cpuMessages.forEach(ele -> {
            CPUMessageVo cpuMessageVo = new CPUMessageVo();
            cpuMessageVo.setCpuTime(ele.getCpuTime());
            cpuMessageVo.setCpuUsed(ele.getCpuUsed());
            cpuMessageVo.setCpuUs(ele.getCpuUs());
            cpuMessageVo.setCpuSy(ele.getCpuSy());
            cpuMessageVo.setCpuNi(ele.getCpuNi());
            cpuMessageVo.setCpuWa(ele.getCpuWa());
            cpuMessageVo.setCpuHi(ele.getCpuHi());
            cpuMessageVo.setCpuSt(ele.getCpuSt());
            cpuMessageVo.setCpuSi(ele.getCpuSi());

            res.add(cpuMessageVo);
        });

        Collections.sort(res);

        return CommonResult.getSuccessInstance(res);
    }

    @Override
    public CommonResult<String> getCpuStartTime(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        Date date = cpuMessageMapper.selectCpuStartTime(serverId);

        if(date == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.CURRENT_SERVER_NOT_HAVE_START_MESSAGE);
        }

        return CommonResult.getSuccessInstance(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public CommonResult<List<CPUMessageVo>> getAssignDateCPUMessage(String beginDate, String endDate, ServerMessage connect) {
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

        List<CpuMessage> cpuMessages = cpuMessageMapper.selectAssignMessage(serverId, beginDate, endDate);

        List<CPUMessageVo> res = new ArrayList<>(cpuMessages.size());

        cpuMessages.forEach(ele -> {
            CPUMessageVo cpuMessageVo = new CPUMessageVo();
            cpuMessageVo.setCpuTime(ele.getCpuTime());
            cpuMessageVo.setCpuUsed(ele.getCpuUsed());
            cpuMessageVo.setCpuUs(ele.getCpuUs());
            cpuMessageVo.setCpuSy(ele.getCpuSy());
            cpuMessageVo.setCpuNi(ele.getCpuNi());
            cpuMessageVo.setCpuWa(ele.getCpuWa());
            cpuMessageVo.setCpuHi(ele.getCpuHi());
            cpuMessageVo.setCpuSt(ele.getCpuSt());
            cpuMessageVo.setCpuSi(ele.getCpuSi());

            res.add(cpuMessageVo);
        });

        Collections.sort(res);

        return CommonResult.getSuccessInstance(res);
    }
}
