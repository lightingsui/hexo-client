package com.lightingsui.linuxwatcher.controller;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.service.ICPUService;
import com.lightingsui.linuxwatcher.vo.CPUMessageVo;
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
 * cpu 信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/8 17:03
 */
@RestController
@RequestMapping("cpu")
@Api(tags = "CPUController", description = "cpu 信息")
public class CPUController {
    @Autowired
    private ICPUService cpuService;

    @ApiOperation("查询内存信息开始时间")
    @RequestMapping(value = "start-time", method = RequestMethod.GET)
    public CommonResult<String> getCpuStartTime(HttpServletRequest request) {
        return cpuService.getCpuStartTime((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询近期内存信息")
    @RequestMapping("recently-cpu-message")
    public CommonResult<List<CPUMessageVo>> getRecentlyCPUMessage(HttpServletRequest request) {
        return cpuService.getRecentlyCPUMessage((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询指定日期内的内存信息")
    @RequestMapping(value = "assign-date-cpu-message", method = RequestMethod.GET)
    public CommonResult<List<CPUMessageVo>> getAssignDateCPUMessage(@ApiParam("开始时间") @RequestParam String beginDate,
                                                                          @ApiParam("结束时间") @RequestParam String endDate,
                                                                          HttpServletRequest request) {
        return cpuService.getAssignDateCPUMessage(beginDate, endDate, (ServerMessage)request.getSession().getAttribute("connect"));
    }
}
