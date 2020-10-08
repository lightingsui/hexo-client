package com.lightingsui.linuxwatcher.controller;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.service.IHardDiskService;
import com.lightingsui.linuxwatcher.vo.HardDiskMessageVo;
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
 * 硬盘信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/7 8:50
 */
@RestController
@RequestMapping("hard-disk")
@Api(tags = "HardDiskController", description = "硬盘信息")
public class HardDiskController {
    @Autowired
    private IHardDiskService hardDiskService;

    @ApiOperation("查询硬盘信息开始时间")
    @RequestMapping(value = "start-time", method = RequestMethod.GET)
    public CommonResult<String> getHardDiskStartTime(HttpServletRequest request) {
        return hardDiskService.getHardDiskStartTime((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询近期硬盘信息")
    @RequestMapping("recently-memory-message")
    public CommonResult<List<HardDiskMessageVo>> getRecentlyHardDiskMessage(HttpServletRequest request) {
        return hardDiskService.getRecentlyHardDiskMessage((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询指定日期内的硬盘信息")
    @RequestMapping(value = "assign-date-hard-disk-message", method = RequestMethod.GET)
    public CommonResult<List<HardDiskMessageVo>> getAssignDateHardDiskMessage(@ApiParam("开始时间") @RequestParam String beginDate,
                                                                          @ApiParam("结束时间") @RequestParam String endDate,
                                                                          HttpServletRequest request) {
        return hardDiskService.getAssignDateHardDiskMessage(beginDate, endDate, (ServerMessage)request.getSession().getAttribute("connect"));
    }
}
