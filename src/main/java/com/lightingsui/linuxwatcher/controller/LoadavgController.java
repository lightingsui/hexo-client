package com.lightingsui.linuxwatcher.controller;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.service.ILoadavgMessage;
import com.lightingsui.linuxwatcher.vo.LoadavgMessageVo;
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
 * 负载信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/9 7:46
 */
@RestController
@RequestMapping("loadavg")
@Api(tags = "LoadavgController", description = "负载信息")
public class LoadavgController {
    @Autowired
    private ILoadavgMessage loadavgMessage;


    @ApiOperation("查询负载信息开始时间")
    @RequestMapping(value = "start-time", method = RequestMethod.GET)
    public CommonResult<String> getLoadavgStartTime(HttpServletRequest request) {
        return loadavgMessage.getLoadavgStartTime((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查看最近负载信息")
    @RequestMapping(value = "recently-network-message", method = RequestMethod.GET)
    public CommonResult<List<LoadavgMessageVo>> getRecentlyLoadavgMessage(HttpServletRequest request) {
        return loadavgMessage.getRecentlyLoadavgMessage((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询指定日期内的负载信息")
    @RequestMapping(value = "assign-date-network-message", method = RequestMethod.GET)
    public CommonResult<List<LoadavgMessageVo>> getAssignDateLoadavgMessage(@ApiParam("开始时间") @RequestParam String beginDate,
                                                                            @ApiParam("结束时间") @RequestParam String endDate,
                                                                            HttpServletRequest request) {
        return loadavgMessage.getAssignDateLoadavgMessage(beginDate, endDate, (ServerMessage)request.getSession().getAttribute("connect"));
    }
}
