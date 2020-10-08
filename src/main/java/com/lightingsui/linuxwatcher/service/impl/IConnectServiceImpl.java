package com.lightingsui.linuxwatcher.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.lightingsui.linuxwatcher.command.LinuxCommand;
import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.common.ErrorResponseCode;
import com.lightingsui.linuxwatcher.common.SuccessResponseCode;
import com.lightingsui.linuxwatcher.config.LogConfig;
import com.lightingsui.linuxwatcher.config.PatternConfig;
import com.lightingsui.linuxwatcher.config.TaskSchedule;
import com.lightingsui.linuxwatcher.mapper.ServerMessageMapper;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.model.ServerMessageUname;
import com.lightingsui.linuxwatcher.service.IConnectService;
import com.lightingsui.linuxwatcher.ssh.SSHHelper;
import com.lightingsui.linuxwatcher.utils.ConversionOfNumberSystems;
import com.lightingsui.linuxwatcher.utils.NetworkUtil;
import com.lightingsui.linuxwatcher.utils.ParseServerMessage;
import com.lightingsui.linuxwatcher.vo.LastLoginMessageVo;
import com.lightingsui.linuxwatcher.vo.ServerMessageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * @author ：隋亮亮
 * @since ：2020/9/23 12:00
 */
@Service
public class IConnectServiceImpl implements IConnectService {
    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public final String COMMAND_RUN_FAILED = "";
    public final String HEXO_COMMAND_NOT_FOUND = "-bash: hexo: command not found";
    public final String NOT_HEXO_FOLDER = "Usage: hexo <command>";
    public final String HEXO_SUB_CONTENT = "/source/_posts/";

    public static final int DATABASE_ERROR = 0;

    private static Object LOCK = new Object();


    @Override
    public CommonResult<Boolean> connect(ServerMessage serverMessage, HttpServletRequest request) {
        // 参数检查
        boolean paramCheck = StringUtils.isBlank(serverMessage.getHost()) || StringUtils.isBlank(serverMessage.getPassword())
                || serverMessage.getPort() == null;

        if (paramCheck) {
            return CommonResult.getErrorInstance(ErrorResponseCode.NESTED_MESSAGE_NOT_SUPPORT);
        }

        // 进行连接并且使用 whoami 进行验证
        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getUser(), serverMessage.getPassword(), serverMessage.getPort());
        boolean connection = sshHelper.getConnection();

        if (connection) {
            String res = sshHelper.execCommand(LinuxCommand.TEST_CONNECT_COMMAND, false);

            if (COMMAND_RUN_FAILED.equals(res)) {
                return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED);
            }

            if (LinuxCommand.DEFAULT_USER.equals(res)) {
                ServerMessageUname systemMessage = getSystemMessage(sshHelper);

                if (serverMessage == null) {
                    return CommonResult.getErrorInstance(ErrorResponseCode.UNAME_GET_ERROR);
                }

                // 根据当前ip查询数据库，查看是否为第一次登录
                com.lightingsui.linuxwatcher.pojo.ServerMessage selectServerMessage = new com.lightingsui.linuxwatcher.pojo.ServerMessage();
                selectServerMessage.setServerIp(serverMessage.getHost());
                com.lightingsui.linuxwatcher.pojo.ServerMessage resServerMessage = serverMessageMapper.selectByCond(selectServerMessage);

                // 第一次登录需要将信息插进数据库
                if (resServerMessage == null) {
                    // 将信息存入到数据库中
                    com.lightingsui.linuxwatcher.pojo.ServerMessage saveToDb = new com.lightingsui.linuxwatcher.pojo.ServerMessage();

                    saveToDb.setServerIp(serverMessage.getHost());
                    saveToDb.setServerName(systemMessage.getNodeName());
                    saveToDb.setServerCreateTime(systemMessage.getKernelVersion());
                    saveToDb.setServerPassword(serverMessage.getPassword());
                    saveToDb.setServerPort(String.valueOf(serverMessage.getPort()));
                    saveToDb.setServerLastTime(new Date());

                    String ipToLocation = NetworkUtil.ipToLocation(request.getRemoteAddr());
                    if (StringUtils.isBlank(ipToLocation)) {
                        return CommonResult.getErrorInstance(ErrorResponseCode.IP_TO_LOCATION_FAILED);
                    }

                    saveToDb.setServerLoginLocation(ipToLocation);
                    saveToDb.setServerOperatingSystem(systemMessage.getOperatingSystem());

                    int effectCount = serverMessageMapper.insertSelective(saveToDb);

                    if (effectCount == DATABASE_ERROR) {
                        return CommonResult.getErrorInstance(ErrorResponseCode.DATABASE_ERROR);
                    }

                    // TODO: 插入信息
                    // 加入到定时任务中
                    com.lightingsui.linuxwatcher.pojo.ServerMessage taskServerMessage = new com.lightingsui.linuxwatcher.pojo.ServerMessage();
                    taskServerMessage.setServerPort(String.valueOf(serverMessage.getPort()));
                    taskServerMessage.setServerPassword(serverMessage.getPassword());
                    taskServerMessage.setServerIp(serverMessage.getHost());
                    TaskSchedule.TASK.add(taskServerMessage);
                }

                // > 连接成功存进 session
                request.getSession().setAttribute("connect", serverMessage);

                return CommonResult.getSuccessInstance(SuccessResponseCode.LOGIN_SUCCESS, true);
            } else {
                LogConfig.log("连接失败，非root用户", LogConfig.ERROR);
                // > 连接不成功直接返回 false
                return CommonResult.getErrorInstance(ErrorResponseCode.UN_ROOT_USER);
            }
        } else {
            LogConfig.log("connect is null, 登录时连接失败", LogConfig.ERROR);
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED);
        }
    }


    @Override
    public CommonResult<ServerMessageVo> getServerMessage(ServerMessage connect) {
        // 参数校验
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        // 使用多线程查询信息
        SSHHelper sshHelper = new SSHHelper(connect.getHost(), connect.getPassword());
        sshHelper.getConnection();
        ServerMessageVo serverMessage = new ServerMessageVo();

        // 使用redis 查询内存硬盘信息
        String memoryTotal = redisTemplate.opsForValue().get(connect.getHost() + "_memoryTotal");
        String memorySwapTotal = redisTemplate.opsForValue().get(connect.getHost() + "_memorySwapTotal");
        String memoryAndSwapTotal = redisTemplate.opsForValue().get(connect.getHost() + "_memoryAndSwapTotal");

        // 使用 redis 查询 硬盘信息
        String hardDiskTotal = redisTemplate.opsForValue().get(connect.getHost() + "_hardDisk");

        boolean hardDiskIsSelect = false;
        boolean memoryIsSelect = false;


        if (!StringUtils.isBlank(hardDiskTotal)) {
            hardDiskIsSelect = true;
        }

        if (!(StringUtils.isBlank(memoryAndSwapTotal) || StringUtils.isBlank(memorySwapTotal)
                || StringUtils.isBlank(memoryTotal))) {
            memoryIsSelect = true;
        }


        CountDownLatch latch = hardDiskIsSelect ? (memoryIsSelect ? new CountDownLatch(3) : new CountDownLatch(4)) :
                (memoryIsSelect ? new CountDownLatch(4) : new CountDownLatch(5));

        if (!memoryIsSelect) {
            new MemoryThread(latch, sshHelper, serverMessage).start();
        } else {
            serverMessage.setMemoryTotal(memoryTotal);
            serverMessage.setMemorySwapTotal(memorySwapTotal);
            serverMessage.setMemoryAndSwapTotal(memoryAndSwapTotal);
        }

        if (!hardDiskIsSelect) {
            new HardDiskThread(latch, sshHelper, serverMessage).start();
        } else {
            serverMessage.setHardDiskTotal(hardDiskTotal);
        }

        new ProcessThread(latch, sshHelper, serverMessage).start();
        new SystemStartUpThread(latch, sshHelper, serverMessage).start();
        new SystemUserCountThread(latch, sshHelper, serverMessage).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return CommonResult.getSuccessInstance(serverMessage);
    }


    /**
     * 获取内存信息
     *
     * @param sshHelper     服务器连接信息
     * @param serverMessage 承载结果
     * @return
     */
    private boolean getMemoryTotal(SSHHelper sshHelper, ServerMessageVo serverMessage) {
        String commandResult = sshHelper.execCommand(LinuxCommand.MEMORY_MESSAGE, false);
        String commandSwapResult = sshHelper.execCommand(LinuxCommand.MEMORY_MESSAGE_SWAP, false);

        if (COMMAND_RUN_FAILED.equals(commandResult) || COMMAND_RUN_FAILED.equals(commandSwapResult)) {
            return false;
        }

        // 匹配数据
        Matcher matcher = PatternConfig.compile.matcher(commandResult);
        Matcher matcherSwap = PatternConfig.compile.matcher(commandSwapResult);

        if (matcher.find() && matcherSwap.find()) {
            String memoryTotal = matcher.group();
            String memorySwapTotal = matcherSwap.group();
            serverMessage.setMemoryTotal(memoryTotal);
            serverMessage.setMemorySwapTotal(memorySwapTotal);
            serverMessage.setMemoryAndSwapTotal(String.valueOf(Integer.parseInt(memorySwapTotal) + Integer.parseInt(memoryTotal)));
        } else {
            return false;
        }

        return true;
    }


    /**
     * 获得硬盘总大小
     *
     * @param sshHelper     连接信息
     * @param serverMessage 承载信息
     * @return
     */
    private boolean getHardDiskTotal(SSHHelper sshHelper, ServerMessageVo serverMessage) {
        String commandResult = sshHelper.execCommand(LinuxCommand.HARD_DISK_TOTAL, false);

        if (COMMAND_RUN_FAILED.equals(commandResult)) {
            return false;
        }

        Matcher matcher = PatternConfig.compile.matcher(commandResult);

        long size = 0L;
        while (matcher.find()) {
            size += Long.parseLong(matcher.group().split(" ")[0]);
        }

        serverMessage.setHardDiskTotal(ConversionOfNumberSystems.byteConverseOther(size, ConversionOfNumberSystems.TO_GB, ConversionOfNumberSystems.ENABLE_UNIT));

        return true;
    }


    /**
     * 获取系统运行时间
     *
     * @param sshHelper
     * @param serverMessage
     * @return
     */
    private boolean getSystemStartUpMessage(SSHHelper sshHelper, ServerMessageVo serverMessage) {
        String commandResult = sshHelper.execCommand(LinuxCommand.SYSTEM_STARTUP_SECOND_TIME, false);

        if (COMMAND_RUN_FAILED.equals(commandResult)) {
            return false;
        }

        int round = Math.round(Float.parseFloat(commandResult));
        DateTime dateTime = DateUtil.offsetSecond(DateUtil.date(), -round);
        String intervalTime = DateUtil.formatBetween(round * 1000);

        serverMessage.setStartUpTime(dateTime.toString());
        serverMessage.setRunTime(intervalTime);
        return true;
    }

    /**
     * 获取系统在线用户数和总用户数
     *
     * @param sshHelper
     * @param serverMessage
     * @return
     */
    private boolean getSystemUserCount(SSHHelper sshHelper, ServerMessageVo serverMessage) {
        String commandResult = sshHelper.execCommand(LinuxCommand.CURRENT_ON_LINE_USER_COUNT, false);
        String commandTotalResult = sshHelper.execCommand(LinuxCommand.CURRENT_SYSTEM_USER_TOTAL, false);

        if (COMMAND_RUN_FAILED.equals(commandResult) || StringUtils.isBlank(commandTotalResult)) {
            return false;
        }

        serverMessage.setCurrentOnLineUserNum(Integer.parseInt(commandResult));
        serverMessage.setTotalUserNum(Integer.parseInt(commandTotalResult));

        return true;
    }

    /**
     * 获取进程信息
     *
     * @param sshHelper     连接信息
     * @param serverMessage 承载信息
     * @return
     */
    private boolean getProcessMessage(SSHHelper sshHelper, ServerMessageVo serverMessage) {
        String commandResult = sshHelper.execCommand(LinuxCommand.PROCESS, false);

        if (COMMAND_RUN_FAILED.equals(commandResult)) {
            return false;
        }

        Matcher matcher = PatternConfig.compile.matcher(commandResult);
        List<String> res = new ArrayList<>();

        while (matcher.find()) {
            res.add(matcher.group());
        }

        serverMessage.setTotalProcess(Integer.parseInt(res.get(0)));
        serverMessage.setRunningProcess(Integer.parseInt(res.get(1)));
        serverMessage.setSleepingProcess(Integer.parseInt(res.get(2)));
        serverMessage.setStoppedProcess(Integer.parseInt(res.get(3)));
        serverMessage.setZombieProcess(Integer.parseInt(res.get(4)));

        return true;
    }

    /**
     * 查询主机信息
     *
     * @param sshHelper 连接信息
     * @return
     */
    private ServerMessageUname getSystemMessage(SSHHelper sshHelper) {
        String commandResult = sshHelper.execCommand(LinuxCommand.UNAME, false);

        if (COMMAND_RUN_FAILED.equals(commandResult)) {
            return null;
        }

        return ParseServerMessage.parse(commandResult);
    }

    /**
     * 获取上次登录信息
     *
     * @param connect
     * @return
     */
    @Override
    public CommonResult<LastLoginMessageVo> getLastLoginMessage(ServerMessage connect, HttpServletRequest request) {
        // 参数校验
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        com.lightingsui.linuxwatcher.pojo.ServerMessage serverMessage = new com.lightingsui.linuxwatcher.pojo.ServerMessage();
        serverMessage.setServerIp(connect.getHost());
        com.lightingsui.linuxwatcher.pojo.ServerMessage selectServerMessage = serverMessageMapper.selectByCond(serverMessage);


        LastLoginMessageVo lastLoginMessageVo = new LastLoginMessageVo();
        lastLoginMessageVo.setLastLoginDate(selectServerMessage.getServerLastTime());
        lastLoginMessageVo.setLastLoginLocation(selectServerMessage.getServerLoginLocation());

        // 更新登录信息
        selectServerMessage.setServerLastTime(new Date());
        selectServerMessage.setServerLoginLocation(NetworkUtil.ipToLocation(request.getRemoteAddr()));
        int effectCount = serverMessageMapper.updateByPrimaryKeySelective(selectServerMessage);

        if (effectCount == DATABASE_ERROR) {
            return CommonResult.getErrorInstance(ErrorResponseCode.DATABASE_ERROR);
        }

        return CommonResult.getSuccessInstance(lastLoginMessageVo);
    }

    @Override
    public CommonResult<String> getWelcomeSpeech(ServerMessage connect) {
        // 参数校验
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        // 使用多线程查询信息
        SSHHelper sshHelper = new SSHHelper(connect.getHost(), connect.getPassword());
        sshHelper.getConnection();

        String commandResult = sshHelper.execCommand(LinuxCommand.LINUX_WELCOME_SPEECH, false);

        if (COMMAND_RUN_FAILED.equals(commandResult)) {
            return null;
        }
        return CommonResult.getSuccessInstance(commandResult);
    }

    @Override
    public CommonResult<ServerMessageUname> getServerMessageUname(ServerMessage connect) {
        // 参数校验
        if (connect == null) {
            return CommonResult.getErrorInstance(ErrorResponseCode.LOGIN_FAILED_UPDATE);
        }

        SSHHelper sshHelper = new SSHHelper(connect.getHost(), connect.getUser(), connect.getPassword(), connect.getPort());
        boolean connection = sshHelper.getConnection();

        if (connection) {
            String res = sshHelper.execCommand(LinuxCommand.UNAME, false);
            sshHelper.disConnect();

            if (COMMAND_RUN_FAILED.equals(res)) {
                LogConfig.log("uname -a 命令执行失败", LogConfig.ERROR);
                return null;
            }

            return CommonResult.getSuccessInstance(ParseServerMessage.parse(res));
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
            String hexoCheck = sshHelper.execCommand(LinuxCommand.HEXO_CHECK, false);

            if (HEXO_COMMAND_NOT_FOUND.equals(hexoCheck)) {
                LogConfig.log("此服务器没有安装 hexo", LogConfig.ERROR);
                return false;
            }


            // 执行 hexo new
            String hexoNewPage = sshHelper.execCommand(LinuxCommand.CD + " " + connect.getPath() + "&&" + LinuxCommand.HEXO_NEW_PAGE + " " + fileName, false);
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
            String generator = sshHelper.execCommand(LinuxCommand.CD + " " + connect.getPath() + " && " + LinuxCommand.HEXO_GENERATE_PAGE, false);


            // 执行hexo -d
            String deploy = sshHelper.execCommand(LinuxCommand.CD + " " + connect.getPath() + " && " + LinuxCommand.HEXO_DEPLOY, false);


            return true;
        }

        LogConfig.log("上传博客时，连接失败", LogConfig.ERROR);

        return false;
    }


    /** 五种信息采集内部类 */
    private class MemoryThread extends Thread {
        private CountDownLatch countDownLatch;
        private SSHHelper sshHelper;
        private ServerMessageVo serverMessage;

        public MemoryThread(CountDownLatch countDownLatch, SSHHelper sshHelper, ServerMessageVo serverMessage) {
            this.countDownLatch = countDownLatch;
            this.sshHelper = sshHelper;
            this.serverMessage = serverMessage;
        }

        @Override
        public void run() {
            try {
                synchronized (LOCK) {
                    getMemoryTotal(sshHelper, serverMessage);
                    redisTemplate.opsForValue().set(sshHelper.getHost() + "_memoryTotal", serverMessage.getMemoryTotal(), 10, TimeUnit.MINUTES);
                    redisTemplate.opsForValue().set(sshHelper.getHost() + "_memorySwapTotal", serverMessage.getMemorySwapTotal(), 10, TimeUnit.MINUTES);
                    redisTemplate.opsForValue().set(sshHelper.getHost() + "_memoryAndSwapTotal", serverMessage.getMemoryAndSwapTotal(), 10, TimeUnit.MINUTES);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.countDownLatch.countDown();
            }
        }
    }

    private class HardDiskThread extends Thread {
        private CountDownLatch countDownLatch;
        private SSHHelper sshHelper;
        private ServerMessageVo serverMessage;

        public HardDiskThread(CountDownLatch countDownLatch, SSHHelper sshHelper, ServerMessageVo serverMessage) {
            this.countDownLatch = countDownLatch;
            this.sshHelper = sshHelper;
            this.serverMessage = serverMessage;
        }

        @Override
        public void run() {
            try {
                synchronized (LOCK) {
                    getHardDiskTotal(sshHelper, serverMessage);
                    redisTemplate.opsForValue().set(sshHelper.getHost() + "_hardDisk", serverMessage.getHardDiskTotal(), 13, TimeUnit.MINUTES);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.countDownLatch.countDown();
            }
        }
    }

    private class ProcessThread extends Thread {
        private CountDownLatch countDownLatch;
        private SSHHelper sshHelper;
        private ServerMessageVo serverMessage;

        public ProcessThread(CountDownLatch countDownLatch, SSHHelper sshHelper, ServerMessageVo serverMessage) {
            this.countDownLatch = countDownLatch;
            this.sshHelper = sshHelper;
            this.serverMessage = serverMessage;
        }

        @Override
        public void run() {
            try {

                synchronized (LOCK) {
                    getProcessMessage(sshHelper, serverMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.countDownLatch.countDown();
            }
        }
    }

    private class SystemStartUpThread extends Thread {
        private CountDownLatch countDownLatch;
        private SSHHelper sshHelper;
        private ServerMessageVo serverMessage;

        public SystemStartUpThread(CountDownLatch countDownLatch, SSHHelper sshHelper, ServerMessageVo serverMessage) {
            this.countDownLatch = countDownLatch;
            this.sshHelper = sshHelper;
            this.serverMessage = serverMessage;
        }

        @Override
        public void run() {
            try {

                synchronized (LOCK) {
                    getSystemStartUpMessage(sshHelper, serverMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.countDownLatch.countDown();
            }
        }
    }

    private class SystemUserCountThread extends Thread {
        private CountDownLatch countDownLatch;
        private SSHHelper sshHelper;
        private ServerMessageVo serverMessage;

        public SystemUserCountThread(CountDownLatch countDownLatch, SSHHelper sshHelper, ServerMessageVo serverMessage) {
            this.countDownLatch = countDownLatch;
            this.sshHelper = sshHelper;
            this.serverMessage = serverMessage;
        }

        @Override
        public void run() {
            try {
                synchronized (LOCK) {
                    getSystemUserCount(sshHelper, serverMessage);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.countDownLatch.countDown();
            }

        }
    }
}
