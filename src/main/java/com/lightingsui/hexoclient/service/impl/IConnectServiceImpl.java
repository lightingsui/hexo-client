package com.lightingsui.hexoclient.service.impl;

import com.lightingsui.hexoclient.command.LinuxCommand;
import com.lightingsui.hexoclient.config.LogConfig;
import com.lightingsui.hexoclient.model.ServerMessage;
import com.lightingsui.hexoclient.model.ServerMessageUname;
import com.lightingsui.hexoclient.service.IConnectService;
import com.lightingsui.hexoclient.ssh.SSHHelper;
import com.lightingsui.hexoclient.utils.ParseServerMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author ：隋亮亮
 * @since ：2020/9/23 12:00
 */
@Service
public class IConnectServiceImpl implements IConnectService {


    public final String COMMAND_RUN_FAILED = "";
    public final String HEXO_COMMAND_NOT_FOUND = "-bash: hexo: command not found";
    public final String NOT_HEXO_FOLDER = "Usage: hexo <command>";
    public final String HEXO_SUB_CONTENT = "/source/_posts/";


    @Override
    public boolean connect(ServerMessage serverMessage, HttpServletRequest request) {
        // 参数检查
        boolean paramCheck = StringUtils.isBlank(serverMessage.getHost()) || StringUtils.isBlank(serverMessage.getPassword())
                || StringUtils.isBlank(serverMessage.getPath());

        if (paramCheck) {
            return false;
        }

        // 进行连接并且使用 whoami 进行验证
        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getUser(), serverMessage.getPassword(), serverMessage.getPort());
        boolean connection = sshHelper.getConnection();

        if (connection) {
            String res = sshHelper.execCommand(LinuxCommand.TEST_CONNECT_COMMAND);
            sshHelper.disConnect();
            if (LinuxCommand.DEFAULT_USER.equals(res)) {
                // > 连接成功存进 session
                request.getSession().setAttribute("connect", serverMessage);
                return true;
            } else {
                LogConfig.log("连接失败，非root用户", LogConfig.ERROR);
                // > 连接不成功直接返回 false
                return false;
            }
        } else {
            LogConfig.log("connect is null, 登录时连接失败", LogConfig.ERROR);
            return false;
        }
    }

    @Override
    public ServerMessageUname getServerMessageUname(ServerMessage connect) {
        SSHHelper sshHelper = new SSHHelper(connect.getHost(), connect.getUser(), connect.getPassword(), connect.getPort());
        boolean connection = sshHelper.getConnection();

        if (connection) {
            String res = sshHelper.execCommand(LinuxCommand.UNAME);
            sshHelper.disConnect();

            if (COMMAND_RUN_FAILED.equals(res)) {
                LogConfig.log("uname -a 命令执行失败", LogConfig.ERROR);
                return null;
            }

            return ParseServerMessage.parse(res);
        }

        LogConfig.log("connect is null, 获取服务器信息时连接失败", LogConfig.ERROR);
        return null;
    }

    @Override
    public boolean uploadBlog(ServerMessage connect, String content, String fileName) {
        // 文件内容转码
        String contentAfterDecode = null;
        try {
            contentAfterDecode = URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogConfig.log("文本内容转码失败", LogConfig.ERROR);
            e.printStackTrace();
            return false;
        }
        final String blogContent = contentAfterDecode;

        // 连接处理
        SSHHelper sshHelper = new SSHHelper(connect.getHost(), connect.getUser(), connect.getPassword(), connect.getPort());
        boolean connection = sshHelper.getConnection();

        if (connection) {


            // 检查是否安装了hexo
            String hexoCheck = sshHelper.execCommand(LinuxCommand.HEXO_CHECK);

            if (HEXO_COMMAND_NOT_FOUND.equals(hexoCheck)) {
                LogConfig.log("此服务器没有安装 hexo", LogConfig.ERROR);
                return false;
            }


            // 执行 hexo new
            String hexoNewPage = sshHelper.execCommand(LinuxCommand.CD + " " + connect.getPath() + "&&" + LinuxCommand.HEXO_NEW_PAGE + " " + fileName);
            if (hexoNewPage.startsWith(NOT_HEXO_FOLDER)) {
                LogConfig.log("指定 hexo 文件夹非 hexo 安装文件夹", LogConfig.ERROR);
                return false;
            }

            // 写入数据
            boolean result = sshHelper.remoteEdit(connect.getPath() + HEXO_SUB_CONTENT + fileName + ".md", (inputLines) -> {
                String outputLines = new String(blogContent);
                return outputLines;
            });

            // 写入失败
            if (!result) {
                LogConfig.log("写入博客内容失败，当前hexo地址: " + connect.getPath() + " 文件名称：" + fileName, LogConfig.ERROR);
                return result;
            }


            // 安装了hexo则进行 hexo -g
            String generator = sshHelper.execCommand(LinuxCommand.CD + " " + connect.getPath() + " && " + LinuxCommand.HEXO_GENERATE_PAGE);


            // 执行hexo -d
            String deploy = sshHelper.execCommand(LinuxCommand.CD + " " + connect.getPath() + " && " + LinuxCommand.HEXO_DEPLOY);


            return true;
        }

        LogConfig.log("上传博客时，连接失败", LogConfig.ERROR);

        return false;
    }
}
