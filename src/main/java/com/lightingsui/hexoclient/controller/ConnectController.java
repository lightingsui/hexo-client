package com.lightingsui.hexoclient.controller;

import com.lightingsui.hexoclient.common.CommonResult;
import com.lightingsui.hexoclient.common.ResponseCode;
import com.lightingsui.hexoclient.model.ServerMessage;
import com.lightingsui.hexoclient.model.ServerMessageUname;
import com.lightingsui.hexoclient.model.UpLoadContent;
import com.lightingsui.hexoclient.service.IConnectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 连接
 *
 * @author ：隋亮亮
 * @since ：2020/9/23 11:59
 */
@RestController
@RequestMapping("server")
@Api(tags = "ConnectController", description = "服务器信息")
public class ConnectController {
    @Autowired
    private IConnectService connectService;

    @ApiOperation("获取连接")
    @RequestMapping(value = "connect", method = RequestMethod.GET)
    public CommonResult<Boolean> connect(@ApiParam("连接信息")  ServerMessage serverMessage,
                                        HttpServletRequest request) {
        return connectService.connect(serverMessage, request) ? CommonResult.getSuccessInstance(ResponseCode.LOGIN_SUCCESS, true) :
                CommonResult.getErrorInstance("请检查服务器信息是否正确");
    }

    @ApiOperation("获取服务器信息")
    @RequestMapping(value = "server-message", method = RequestMethod.GET)
    public CommonResult<ServerMessageUname> getServerMessageUname(HttpServletRequest request) {
        ServerMessageUname messageUname = connectService.getServerMessageUname((ServerMessage)request.getSession().getAttribute("connect"));

        return messageUname == null ? CommonResult.getErrorInstance("服务器信息解析错误") :
                CommonResult.getSuccessInstance(messageUname);
    }

    @ApiOperation("上传博客")
    @RequestMapping(value = "upload-blog", method = RequestMethod.POST)
    public CommonResult<Boolean> uploadBlog(@RequestBody UpLoadContent upLoadContent, HttpServletRequest request) {
        return connectService.uploadBlog((ServerMessage)request.getSession().getAttribute("connect"), upLoadContent.getContent(), upLoadContent.getFileName()) ?
                CommonResult.getSuccessInstance(true) : CommonResult.getErrorInstance("上传失败");
    }


}
