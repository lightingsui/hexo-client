package com.lightingsui.linuxwatcher.controller;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.model.ServerMessageUname;
import com.lightingsui.linuxwatcher.model.UpLoadContent;
import com.lightingsui.linuxwatcher.service.IConnectService;
import com.lightingsui.linuxwatcher.vo.LastLoginMessageVo;
import com.lightingsui.linuxwatcher.vo.ServerMessageVo;
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
        return connectService.connect(serverMessage, request);
    }

    @ApiOperation("获取上次登录信息")
    @RequestMapping(value = "last-login-message", method = RequestMethod.GET)
    public CommonResult<LastLoginMessageVo> getLastLoginMessage(HttpServletRequest request) {
        return connectService.getLastLoginMessage((ServerMessage)request.getSession().getAttribute("connect"), request);
    }


    @ApiOperation("获取服务器信息")
    @RequestMapping(value = "server-message", method = RequestMethod.GET)
    public CommonResult<ServerMessageVo> getServerMessage(HttpServletRequest request) {
        return connectService.getServerMessage((ServerMessage)request.getSession().getAttribute("connect"));
    }

    @ApiOperation("获取欢迎语")
    @RequestMapping(value = "welcome_speech", method = RequestMethod.GET)
    public CommonResult<String> getWelcomeSpeech(HttpServletRequest request) {
        return connectService.getWelcomeSpeech((ServerMessage)request.getSession().getAttribute("connect"));
    }

    @ApiOperation("获取服务器系统信息")
    @RequestMapping(value = "uname-message", method = RequestMethod.GET)
    public CommonResult<ServerMessageUname> getUnameMessage(HttpServletRequest request) {
        return connectService.getServerMessageUname((ServerMessage)request.getSession().getAttribute("connect"));
    }












//    @ApiOperation("获取服务器信息")
//    @RequestMapping(value = "server-message", method = RequestMethod.GET)
//    public CommonResult<ServerMessageUname> getServerMessageUname(HttpServletRequest request) {
//        ServerMessageUname messageUname = connectService.getServerMessageUname((ServerMessage)request.getSession().getAttribute("connect"));
//
//        return messageUname == null ? CommonResult.getErrorInstance("服务器信息解析错误") :
//                CommonResult.getSuccessInstance(messageUname);
//    }











    @ApiOperation("上传博客")
    @RequestMapping(value = "upload-blog", method = RequestMethod.POST)
    public CommonResult<Boolean> uploadBlog(@RequestBody UpLoadContent upLoadContent, HttpServletRequest request) {
        return connectService.uploadBlog((ServerMessage)request.getSession().getAttribute("connect"), upLoadContent.getContent(), upLoadContent.getFileName()) ?
                CommonResult.getSuccessInstance(true) : CommonResult.getErrorInstance("上传失败");
    }


}
