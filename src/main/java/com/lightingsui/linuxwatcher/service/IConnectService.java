package com.lightingsui.linuxwatcher.service;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.model.ServerMessageUname;
import com.lightingsui.linuxwatcher.vo.LastLoginMessageVo;
import com.lightingsui.linuxwatcher.vo.ServerMessageVo;

import javax.servlet.http.HttpServletRequest;

/**
 * 连接
 *
 * @author ：隋亮亮
 * @since ：2020/9/23 12:00
 */
public interface IConnectService {
    CommonResult<Boolean> connect(ServerMessage serverMessage, HttpServletRequest request);

    CommonResult<ServerMessageUname> getServerMessageUname(ServerMessage connect);

    CommonResult<LastLoginMessageVo> getLastLoginMessage(ServerMessage connect, HttpServletRequest  request);



    boolean uploadBlog(ServerMessage connect, String content, String fileName);

    CommonResult<ServerMessageVo> getServerMessage(ServerMessage connect);

    CommonResult<String> getWelcomeSpeech(ServerMessage connect);
}
