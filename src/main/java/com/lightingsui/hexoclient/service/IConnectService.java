package com.lightingsui.hexoclient.service;

import com.lightingsui.hexoclient.model.ServerMessage;
import com.lightingsui.hexoclient.model.ServerMessageUname;

import javax.servlet.http.HttpServletRequest;

/**
 * 连接
 *
 * @author ：隋亮亮
 * @since ：2020/9/23 12:00
 */
public interface IConnectService {
    boolean connect(ServerMessage serverMessage, HttpServletRequest request);

    ServerMessageUname getServerMessageUname(ServerMessage connect);

    boolean uploadBlog(ServerMessage connect, String content, String fileName);
}
