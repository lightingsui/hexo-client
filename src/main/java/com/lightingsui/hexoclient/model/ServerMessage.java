package com.lightingsui.hexoclient.model;

import lombok.Data;

/**
 * 服务器信息
 *
 * @author ：隋亮亮
 * @since ：2020/9/23 12:01
 */
@Data
public class ServerMessage {
    private String host;
    private Integer port;
    private String user;
    private String password;

    private String path;

    public ServerMessage() {
        user = "root";
    }
}
