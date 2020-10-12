package com.lightingsui.linuxwatcher.service.impl;

import com.jcraft.jsch.JSchException;
import com.lightingsui.linuxwatcher.command.LinuxCommand;
import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.common.ErrorResponseCode;
import com.lightingsui.linuxwatcher.exception.DatabaseException;
import com.lightingsui.linuxwatcher.mapper.HexoMapper;
import com.lightingsui.linuxwatcher.mapper.ServerMessageMapper;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.service.IHexoBlogService;
import com.lightingsui.linuxwatcher.ssh.SSHControl;
import com.lightingsui.linuxwatcher.ssh.SSHHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author ：隋亮亮
 * @since ：2020/10/11 11:57
 */
@Service
public class IHexoBlogServiceImpl implements IHexoBlogService {
    private final static Logger LOGGER = Logger.getLogger(IHexoBlogServiceImpl.class);
    private final static String HEXO_FOLDER_PREFIX = "_hexo";
    private final static String HEXO_TEMPLATE_REDIS_SUFFIX = "_template";

    public static final String HEXO_CHECK_FAILED = "-bash: hexo: command not found";
    public static final String HEXO_CHECK_FAILED_ANOTHER = "Usage: hexo <command>Commands:  help     Get help on a command.";
    public final String HEXO_SUB_CONTENT = "/source/_posts/";

    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private HexoMapper hexoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public CommonResult<Boolean> setHexoFolder(String hexoFolderLocation, ServerMessage connect) {
        // 登录校验
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        // 参数校验
        if (StringUtils.isBlank(hexoFolderLocation)) {
            return CommonResult.getErrorInstance(ErrorResponseCode.HEXO_FOLDER_MUST_BE_PROVIDED);
        }

        SSHHelper sshHelper = new SSHHelper(connect.getHost(), connect.getPassword(), connect.getPort());
        try {
            sshHelper.getConnection();
        } catch (JSchException e) {
            LOGGER.error("在设置hexo文件夹时，主机连接获取失败");
            LOGGER.error(e);
            e.printStackTrace();
            return CommonResult.getErrorInstance(ErrorResponseCode.SERVICE_CONNECT_FAILED);
        }

        boolean checkResult = hexoFolderCheck(hexoFolderLocation, sshHelper);

        sshHelper.disConnect();

        if (!checkResult) {
            return CommonResult.getErrorInstance(ErrorResponseCode.HEXO_FOLDER_CHECK_FAILED);
        }

        redisTemplate.opsForValue().set(connect.getHost() + HEXO_FOLDER_PREFIX, hexoFolderLocation);

        com.lightingsui.linuxwatcher.pojo.ServerMessage serverMessage = new com.lightingsui.linuxwatcher.pojo.ServerMessage();

        serverMessage.setServerIp(connect.getHost());

        try {
            com.lightingsui.linuxwatcher.pojo.ServerMessage serverMessageResult
                    = serverMessageMapper.selectByCond(serverMessage);

            serverMessageResult.setServerHexoAddress(hexoFolderLocation);

            serverMessageMapper.updateByPrimaryKeySelective(serverMessageResult);
        } catch (Exception e) {
            LOGGER.error("在修改hexo文件夹位置时，数据库出现异常");
            LOGGER.error(e);
            e.printStackTrace();

            return CommonResult.getErrorInstance(ErrorResponseCode.DATABASE_ERROR);
        }

        return CommonResult.getSuccessInstance(true);
    }

    @Override
    public CommonResult<String> getHexoFolderLocation(ServerMessage connect) {
        // 登录校验
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        String location = redisTemplate.opsForValue().get(connect.getHost() + HEXO_FOLDER_PREFIX);

        // redis 找不到
        if (StringUtils.isBlank(location)) {
            location = serverMessageMapper.selectUserHexoLocaltion(connect.getHost());

            // localtion 未设置时
            if (!StringUtils.isBlank(location)) {
                // 存放进redis
                redisTemplate.opsForValue().set(connect.getHost() + HEXO_FOLDER_PREFIX, location);
            }

        }

        return CommonResult.getSuccessInstance(location);
    }

    @Override
    public CommonResult<String> getHexoTemplate(ServerMessage connect) {
        // 登录校验
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        String template = redisTemplate.opsForValue().get(connect.getHost() + HEXO_TEMPLATE_REDIS_SUFFIX);

        if (StringUtils.isBlank(template)) {
            LOGGER.info(connect.getHost() + " 查询hexo模板时从数据库中查询");

            try {
                template = hexoMapper.selectUserTemplate(connect.getHost());
            } catch (Exception e) {
                LOGGER.error("获取模板时查询数据库出现异常");
                LOGGER.error(e);
                e.printStackTrace();
                return CommonResult.getErrorInstance(ErrorResponseCode.DATABASE_ERROR);
            }

            redisTemplate.opsForValue().set(connect.getHost() + HEXO_TEMPLATE_REDIS_SUFFIX, template);
        } else {
            LOGGER.info(connect.getHost() + " 查询数据库模板时从redis中查询");
        }


        return CommonResult.getSuccessInstance(template);
    }

    @Override
    public CommonResult<Boolean> setHexoTemplate(String hexoTemplate, ServerMessage connect) {
        // 登录校验
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        if (StringUtils.isBlank(hexoTemplate)) {
            return CommonResult.getErrorInstance(ErrorResponseCode.HEXO_TEMPLATE_CHECK_FAILED);
        }

        redisTemplate.opsForValue().set(connect.getHost() + HEXO_TEMPLATE_REDIS_SUFFIX, hexoTemplate);

        try {
            int serverId = serverMessageMapper.selectServerIdByHost(connect.getHost());

            hexoMapper.updateByServerId(serverId, hexoTemplate);
        } catch (DatabaseException e) {
            LOGGER.error("设置模板时数据库出现异常");
            LOGGER.error(e);
            e.printStackTrace();
            return CommonResult.getErrorInstance(ErrorResponseCode.DATABASE_ERROR);
        }

        return CommonResult.getSuccessInstance(true);
    }

    @Override
    public CommonResult<Boolean> uploadBlog(ServerMessage connect, String content, String fileName) {
        // 文件内容转码
        String contentAfterDecode = null;
        try {
            contentAfterDecode = URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("上传博客时，URL参数解码失败");
            LOGGER.error(e);
            e.printStackTrace();
            return CommonResult.getErrorInstance(ErrorResponseCode.URL_DECODE_FAILED);
        }
        final String blogContent = contentAfterDecode;

        // 连接处理
        SSHHelper sshHelper = new SSHHelper(connect.getHost(), connect.getUser(), connect.getPassword(), connect.getPort());
        boolean connection = false;
        try {
            connection = sshHelper.getConnection();
        } catch (JSchException e) {
            LOGGER.error("上传博客时，获取服务器连接失败");
            LOGGER.error(e);
            e.printStackTrace();
            return CommonResult.getErrorInstance(ErrorResponseCode.SERVICE_CONNECT_FAILED);
        }

        if (connection) {
            CommonResult<String> hexoFolderLocation = getHexoFolderLocation(connect);

            // 执行 hexo new
            String hexoNewCommand = LinuxCommand.CD  + hexoFolderLocation.getData()
                    + LinuxCommand.LinuxCommandSeparator.PARALLEL + LinuxCommand.HEXO_NEW_PAGE + " " + fileName;

            String hexoNewPage = sshHelper.execCommand(hexoNewCommand, SSHControl.UN_ENABLE_ENTER);
            LOGGER.info("执行命令： " + hexoNewCommand);



            String filePathAndName = hexoFolderLocation.getData() + HEXO_SUB_CONTENT + fileName + ".md";
            LOGGER.info("文件路径名称：" + filePathAndName);
            // 写入数据
            boolean result = sshHelper.remoteEdit(filePathAndName, (inputLines) -> {
                String outputLines = new String(blogContent);
                return outputLines;
            });

            // 写入失败
            if (!result) {
                LOGGER.error("写入博客内容失败，当前hexo地址: " + hexoFolderLocation.getData() + " 文件名称：" + fileName);
                return CommonResult.getErrorInstance(ErrorResponseCode.WRITE_SERVER_BLOG_FAILED);
            }


            // 安装了hexo则进行 hexo -g
            String hexoGCommand = LinuxCommand.CD  + hexoFolderLocation.getData()
                    + LinuxCommand.LinuxCommandSeparator.PARALLEL + LinuxCommand.HEXO_GENERATE_PAGE;

            String generator = sshHelper.execCommand(hexoGCommand, SSHControl.UN_ENABLE_ENTER);

            LOGGER.info("执行命令： " + hexoGCommand);


            // 执行hexo -d
            String hexoDCommand = LinuxCommand.CD  + hexoFolderLocation.getData()
                    + LinuxCommand.LinuxCommandSeparator.PARALLEL + LinuxCommand.HEXO_DEPLOY;

            String deploy = sshHelper.execCommand(hexoDCommand, SSHControl.UN_ENABLE_ENTER);

            LOGGER.info("执行命令： " + hexoDCommand);
            sshHelper.disConnect();

            return CommonResult.getSuccessInstance(true);
        }

        LOGGER.error("上传博客时，获取服务器连接失败");

        return CommonResult.getErrorInstance(ErrorResponseCode.SERVICE_CONNECT_FAILED);
    }


    /**
     * 检查hexo文件夹是否正确
     *
     * @param hexoFolderLocation hexo文件夹位置
     * @param sshHelper          连接
     * @return {@code true} 位置正确<br>{@code false} 位置不正确
     */
    private boolean hexoFolderCheck(String hexoFolderLocation, SSHHelper sshHelper) {
        String command = LinuxCommand.CD + hexoFolderLocation + LinuxCommand.LinuxCommandSeparator.PARALLEL + LinuxCommand.HEXO_CHECK;
        String commandResult = sshHelper.execCommand(command, SSHControl.UN_ENABLE_ENTER);

        if (commandResult.contains(HEXO_CHECK_FAILED) || commandResult.contains(HEXO_CHECK_FAILED_ANOTHER) || StringUtils.isBlank(commandResult)) {
            return false;
        } else {
            return true;
        }
    }
}
