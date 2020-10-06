package com.lightingsui.linuxwatcher.controller;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.service.IMemoryService;
import com.lightingsui.linuxwatcher.vo.MemoryMessageVo;
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
 * 内存信息
 *
 * @author ：隋亮亮
 * @since ：2020/10/6 17:31
 */
@RestController
@RequestMapping("memory")
@Api(tags = "MemoryController", description = "内存信息")
public class MemoryController {
    @Autowired
    private IMemoryService memoryService;

    @ApiOperation("查询内存信息开始时间")
    @RequestMapping(value = "start-time", method = RequestMethod.GET)
    public CommonResult<String> getMemoryStartTime(HttpServletRequest request) {
        return memoryService.getMemoryStartTime((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询近期内存信息")
    @RequestMapping("recently-memory-message")
    public CommonResult<List<MemoryMessageVo>> getRecentlyMemoryMessage(HttpServletRequest request) {
        return memoryService.getRecentlyMemoryMessage((ServerMessage) request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查询指定日期内的内存信息")
    @RequestMapping(value = "assign-date-memory-message", method = RequestMethod.GET)
    public CommonResult<List<MemoryMessageVo>> getAssignDateMemoryMessage(@ApiParam("开始时间") @RequestParam String beginDate,
                                                                            @ApiParam("结束时间") @RequestParam String endDate,
                                                                            HttpServletRequest request) {
        return memoryService.getAssignDateMemoryMessage(beginDate, endDate, (ServerMessage)request.getSession().getAttribute("connect"));
    }
}
