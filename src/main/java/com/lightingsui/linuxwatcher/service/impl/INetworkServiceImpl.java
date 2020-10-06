package com.lightingsui.linuxwatcher.service.impl;

import cn.hutool.core.date.DateUtil;
import com.lightingsui.linuxwatcher.command.LinuxCommand;
import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.common.ErrorResponseCode;
import com.lightingsui.linuxwatcher.mapper.NetworkMessageMapper;
import com.lightingsui.linuxwatcher.mapper.ServerMessageMapper;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.pojo.NetworkMessage;
import com.lightingsui.linuxwatcher.service.INetworkService;
import com.lightingsui.linuxwatcher.ssh.SSHHelper;
import com.lightingsui.linuxwatcher.vo.NetwrokMessageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：隋亮亮
 * @since ：2020/10/5 16:41
 */
@Service
public class INetworkServiceImpl implements INetworkService {
    private final static String NOT_INSTALL_DSTAT = "not found";
    private final static int RECENTLY_TIME = 8;

    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private NetworkMessageMapper networkMessageMapper;

    /**
     * 检查是否安装了 {@code dstat}
     * @param connect 连接信息
     * @return
     */
    @Override
    public CommonResult<Boolean> checkInstallDstat(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        SSHHelper sshHelper = new SSHHelper(connect.getHost(), connect.getPassword(), connect.getPort());
        sshHelper.getConnection();

        String commandResult = sshHelper.execCommand(LinuxCommand.CHECK_INSTALL_DSTAT, false);
        if(StringUtils.isBlank(commandResult)) {
            return CommonResult.getErrorInstance(ErrorResponseCode.UNKNOWN_EXCEPTION);
        }

        if(commandResult.contains(NOT_INSTALL_DSTAT)) {
            return CommonResult.getErrorInstance(ErrorResponseCode.NOT_INSTALL_DSTAT);
        } else {
            return CommonResult.getSuccessInstance(true);
        }
    }

    @Override
    public CommonResult<String> getNetworkStartTime(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        Date date = networkMessageMapper.selectNetworkStartTime(serverId);

        if(date == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.CURRENT_SERVER_NOT_HAVE_START_MESSAGE);
        }

        return CommonResult.getSuccessInstance(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public CommonResult<List<NetwrokMessageVo>> getRecentlyNetworkMessage(ServerMessage connect) {
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }
        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<NetworkMessage> networkMessages = networkMessageMapper.selectRecentlyMessage(serverId, RECENTLY_TIME);

        List<NetwrokMessageVo> res = new ArrayList<>(networkMessages.size());

        networkMessages.forEach(ele -> {
            NetwrokMessageVo netwrokMessageVo = new NetwrokMessageVo();
            netwrokMessageVo.setInSpeed(ele.getNetworkInSpeed());
            netwrokMessageVo.setOutSpeed(ele.getNetworkOutSpeed());
            netwrokMessageVo.setTime(ele.getNetworkTime());

            res.add(netwrokMessageVo);
        });


        return CommonResult.getSuccessInstance(res);
    }

    @Override
    public CommonResult<List<NetwrokMessageVo>> getAssignDateNetworkMessage(String beginDate, String endDate, ServerMessage connect) {
        // 登录验证
        if(connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        // 必备参数验证
        if(StringUtils.isBlank(beginDate) || StringUtils.isBlank(endDate)) {
            return CommonResult.getErrorInstance(ErrorResponseCode.ERROR_BEGIN_END_DATE);
        }
        int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

        List<NetworkMessage> networkMessages = networkMessageMapper.selectAssignMessage(serverId, beginDate, endDate);

        List<NetwrokMessageVo> res = new ArrayList<>(networkMessages.size());

        networkMessages.forEach(ele -> {
            NetwrokMessageVo netwrokMessageVo = new NetwrokMessageVo();
            netwrokMessageVo.setInSpeed(ele.getNetworkInSpeed());
            netwrokMessageVo.setOutSpeed(ele.getNetworkOutSpeed());
            netwrokMessageVo.setTime(ele.getNetworkTime());

            res.add(netwrokMessageVo);
        });

        return CommonResult.getSuccessInstance(res);
    }
}