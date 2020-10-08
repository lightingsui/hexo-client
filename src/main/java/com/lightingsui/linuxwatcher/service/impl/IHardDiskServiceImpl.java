package com.lightingsui.linuxwatcher.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.common.ErrorResponseCode;
import com.lightingsui.linuxwatcher.mapper.HardDiskMessageMapper;
import com.lightingsui.linuxwatcher.mapper.ServerMessageMapper;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.pojo.HardDiskMessage;
import com.lightingsui.linuxwatcher.service.IHardDiskService;
import com.lightingsui.linuxwatcher.vo.HardDiskMessageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：隋亮亮
 * @since ：2020/10/7 8:52
 */
@Service
public class IHardDiskServiceImpl implements IHardDiskService {
    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private HardDiskMessageMapper hardDiskMessageMapper;


    @Override
    public CommonResult<String> getHardDiskStartTime(ServerMessage connect) {
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        Date date = hardDiskMessageMapper.selectHardDiskStartTime(serverId);

        if (date == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.CURRENT_SERVER_NOT_HAVE_START_MESSAGE);
        }

        return CommonResult.getSuccessInstance(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public CommonResult<List<HardDiskMessageVo>> getRecentlyHardDiskMessage(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }
        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<HardDiskMessage> hardDiskMessages = hardDiskMessageMapper.selectRecentlyMessage(serverId, INetworkServiceImpl.RECENTLY_TIME);

        List<HardDiskMessageVo> res = new ArrayList<>(hardDiskMessages.size());

        hardDiskMessages.forEach(ele -> {
            HardDiskMessageVo hardDiskMessageVo = new HardDiskMessageVo();
            hardDiskMessageVo.setUsed(ele.getHardDiskUsed());
            hardDiskMessageVo.setUsabled(ele.getHardDiskUsable());
            hardDiskMessageVo.setTime(ele.getHardDiskTime());

            res.add(hardDiskMessageVo);
        });


        return CommonResult.getSuccessInstance(res);
    }

    @Override
    public CommonResult<List<HardDiskMessageVo>> getAssignDateHardDiskMessage(String beginDate, String endDate, ServerMessage connect) {
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
            return CommonResult.getErrorInstance(ErrorResponseCode.BEGIN_MUST_LT_END);
        }

        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<HardDiskMessage> hardDiskMessages = hardDiskMessageMapper.selectAssignMessage(serverId, beginDate, endDate);

        List<HardDiskMessageVo> res = new ArrayList<>(hardDiskMessages.size());

        hardDiskMessages.forEach(ele -> {
            HardDiskMessageVo hardDiskMessageVo = new HardDiskMessageVo();
            hardDiskMessageVo.setUsed(ele.getHardDiskUsed());
            hardDiskMessageVo.setUsabled(ele.getHardDiskUsable());
            hardDiskMessageVo.setTime(ele.getHardDiskTime());

            res.add(hardDiskMessageVo);
        });

        return CommonResult.getSuccessInstance(res);
    }
}
