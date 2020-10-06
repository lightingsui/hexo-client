package com.lightingsui.linuxwatcher.controller;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.service.INetworkService;
import com.lightingsui.linuxwatcher.vo.NetwrokMessageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 网络信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/5 16:39
 */
@RestController
@Api(tags = "NetworkController", description = "网络信息")
@RequestMapping("network")
public class NetworkController {
    @Autowired
    private INetworkService networkService;


    @ApiOperation("查询是否安装了dstat")
    @RequestMapping(value = "install-datst", method = RequestMethod.GET)
    public CommonResult<Boolean> checkInstallDstat(HttpServletRequest request) {
        return networkService.checkInstallDstat((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询网络信息开始时间")
    @RequestMapping(value = "start-time", method = RequestMethod.GET)
    public CommonResult<String> getNetworkStartTime(HttpServletRequest request) {
        return networkService.getNetworkStartTime((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查看最近网络信息")
    @RequestMapping(value = "recently-network-message", method = RequestMethod.GET)
    public CommonResult<List<NetwrokMessageVo>> getRecentlyNetworkMessage(HttpServletRequest request) {
        return networkService.getRecentlyNetworkMessage((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询指定日期内的网络信息")
    @RequestMapping(value = "assign-date-network-message", method = RequestMethod.GET)
    public CommonResult<List<NetwrokMessageVo>> getAssignDateNetworkMessage(@ApiParam("开始时间") @RequestParam String beginDate,
                                                                      @ApiParam("结束时间") @RequestParam String endDate,
                                                                      HttpServletRequest request) {
        return networkService.getAssignDateNetworkMessage(beginDate, endDate, (ServerMessage)request.getSession().getAttribute("connect"));
    }
}
