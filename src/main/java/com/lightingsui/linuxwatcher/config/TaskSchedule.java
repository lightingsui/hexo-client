package com.lightingsui.linuxwatcher.config;

import com.jcraft.jsch.JSchException;
import com.lightingsui.linuxwatcher.command.LinuxCommand;
import com.lightingsui.linuxwatcher.exception.DatabaseException;
import com.lightingsui.linuxwatcher.mapper.*;
import com.lightingsui.linuxwatcher.pojo.*;
import com.lightingsui.linuxwatcher.service.impl.IConnectServiceImpl;
import com.lightingsui.linuxwatcher.ssh.SSHControl;
import com.lightingsui.linuxwatcher.ssh.SSHHelper;
import com.lightingsui.linuxwatcher.utils.ConversionOfNumberSystems;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 定时监测任务
 *
 * @author ：隋亮亮
 * @since ：2020/10/6 8:37
 */
@Component
public class TaskSchedule {


    public final Logger LOGGER = Logger.getLogger(TaskSchedule.class);

    public static final List<ServerMessage> TASK = new ArrayList<>();
    private static final int RUN_INTERVAL = 5000;

    private boolean once = true;

    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private NetworkMessageMapper networkMessageMapper;
    @Autowired
    private MemoryMessageMapper memoryMessageMapper;
    @Autowired
    private HardDiskMessageMapper hardDiskMessageMapper;
    @Autowired
    private CpuMessageMapper cpuMessageMapper;
    @Autowired
    private LoadavgMessageMapper loadavgMessageMapper;


    @Scheduled(fixedDelay = RUN_INTERVAL)
    public void taskRun() {
        if (once) {
            // 启动时先查询出所有服务器信息
            TASK.addAll(serverMessageMapper.selectAllOnlyServerId());

            once = false;
        } else {
            LOGGER.info("开始收集数据");

            for (int i = 0; i < TASK.size(); i++) {
                ServerMessage serverMessage = TASK.get(i);
                SSHHelper sshHelper = new SSHHelper(serverMessage.getServerIp(), serverMessage.getServerPassword(),
                        Integer.parseInt(serverMessage.getServerPort()));

                try {
                    sshHelper.getConnection();
                } catch (JSchException e) {
                    LOGGER.error(serverMessage.getServerIp() + "服务器连接异常，请及时处理");
                    e.printStackTrace();
                    continue;
                }

                LOGGER.info(serverMessage.getServerIp() + " 正在收集数据");
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            resolveNetwork(sshHelper, serverMessage);
                            resolveMemory(sshHelper, serverMessage);
                            resolveHardDisk(sshHelper, serverMessage);
                            resolveCPU(sshHelper, serverMessage);
                            resolveLoadavg(sshHelper, serverMessage);
                        } catch (DatabaseException e) {
                            LOGGER.error(e);
                            LOGGER.error("数据库异常，暂停一切定时任务");
                            ThreadPoolConfig.executor.shutdownNow();
                            e.printStackTrace();
                        }  finally {
                            LOGGER.info(serverMessage.getServerIp() + " 数据收集结束");
                            sshHelper.disConnect();
                        }
                    }
                };

                ThreadPoolConfig.executor.execute(runnable);


            }
        }
    }

    /**
     * 处理网络信息
     *
     * @param sshHelper     连接处理器
     * @param serverMessage 当前服务器信息
     */
    private void resolveNetwork(SSHHelper sshHelper, ServerMessage serverMessage) {
        try {
            String dstatCheck = sshHelper.execCommand(LinuxCommand.CHECK_INSTALL_DSTAT, SSHControl.UN_ENABLE_ENTER);

            if(StringUtils.isBlank(dstatCheck)) {
                LOGGER.error(serverMessage.getServerIp() + "not install dstat");
                return;
            }
        } catch (DatabaseException e) {
            LOGGER.error(serverMessage.getServerIp() + "not install dstat");
//            e.printStackTrace();
            return;
        }
        String s = sshHelper.execCommand(LinuxCommand.GET_SYSTEM_NETWORK_SPEED, SSHControl.ENABLE_ENTER);

        // 格式化命令输出
        String res = s.split("\n")[0];
        res = res.trim();
        String[] s1 = res.split(" ");

        Long r1 = ConversionOfNumberSystems.OtherToBytes(s1[0]);
        Long r2 = ConversionOfNumberSystems.OtherToBytes(s1[s1.length - 1]);

        String out = ConversionOfNumberSystems.byteConverseOther(r1, ConversionOfNumberSystems.TO_KB, ConversionOfNumberSystems.ENABLE_UNIT);
        String in = ConversionOfNumberSystems.byteConverseOther(r2, ConversionOfNumberSystems.TO_KB, ConversionOfNumberSystems.ENABLE_UNIT);


        // 存储
        NetworkMessage networkMessage = new NetworkMessage();
        networkMessage.setNetworkInSpeed(in);
        networkMessage.setNetworkOutSpeed(out);
        networkMessage.setNetworkTime(new Date());
        networkMessage.setServerId(serverMessage.getServerId());

        int effectCount = networkMessageMapper.insertSelective(networkMessage);

        if (effectCount == IConnectServiceImpl.DATABASE_ERROR) {
            throw new DatabaseException("数据库异常");
        }
    }

    /**
     * 处理内存信息
     *
     * @param sshHelper
     * @param serverMessage
     */
    private void resolveMemory(SSHHelper sshHelper, ServerMessage serverMessage) {
        String memoryTotal = sshHelper.execCommand(LinuxCommand.MEMORY_MESSAGE, SSHControl.UN_ENABLE_ENTER);
        String swapTotal = sshHelper.execCommand(LinuxCommand.MEMORY_MESSAGE_SWAP, SSHControl.UN_ENABLE_ENTER);


        Matcher memoryTotalMatcher = PatternConfig.compile.matcher(memoryTotal);
        List<Integer> totalList = new ArrayList<>();

        while (memoryTotalMatcher.find()) {
            totalList.add(Integer.parseInt(memoryTotalMatcher.group()));
        }

        Matcher swapMemoryTotalMatcher = PatternConfig.compile.matcher(swapTotal);
        List<Integer> swapMemoryList = new ArrayList<>();

        while (swapMemoryTotalMatcher.find()) {
            swapMemoryList.add(Integer.parseInt(swapMemoryTotalMatcher.group()));
        }

        int used = totalList.get(1) + swapMemoryList.get(1);
        int usable = totalList.get(5) + swapMemoryList.get(2);

        MemoryMessage memoryMessage = new MemoryMessage();
        memoryMessage.setMemoryTime(new Date());
        memoryMessage.setMemoryUsable(String.valueOf(usable));
        memoryMessage.setMemoryUsed(String.valueOf(used));
        memoryMessage.setServerId(serverMessage.getServerId());

        int effectCount = memoryMessageMapper.insertSelective(memoryMessage);

        if (effectCount == IConnectServiceImpl.DATABASE_ERROR) {
            throw new DatabaseException("数据库异常");
        }

    }

    /**
     * 处理硬盘信息
     *
     * @param sshHelper
     * @param serverMessage
     */
    private void resolveHardDisk(SSHHelper sshHelper, ServerMessage serverMessage) {
        String hardDiskUsed = sshHelper.execCommand(LinuxCommand.HARD_DISK_USED, SSHControl.ENABLE_ENTER);
        String hardDiskTotal = sshHelper.execCommand(LinuxCommand.HARD_DISK_TOTAL_UPGRADE, SSHControl.ENABLE_ENTER);


        // 获取使用量
        List<Float> res = new ArrayList<>();

        Matcher matcher = PatternConfig.hardDiskCompile.matcher(hardDiskUsed);

        while (matcher.find()) {
            String group = matcher.group();
            Long tmp = ConversionOfNumberSystems.OtherToBytes(group);
            String GB = ConversionOfNumberSystems.byteConverseOther(tmp, ConversionOfNumberSystems.TO_GB, ConversionOfNumberSystems.UN_ENABLE_UNIT);

            res.add(Float.parseFloat(GB));
        }

        Float used = res.stream().reduce(Float::sum).get();


        // 获取总量
        List<Float> resTotal = new ArrayList<>();
        Matcher totalMatcher = PatternConfig.hardDiskCompile.matcher(hardDiskTotal);

        while (totalMatcher.find()) {
            String group = totalMatcher.group();
            Long tmp = ConversionOfNumberSystems.OtherToBytes(group);
            String GB = ConversionOfNumberSystems.byteConverseOther(tmp, ConversionOfNumberSystems.TO_GB, ConversionOfNumberSystems.UN_ENABLE_UNIT);

            resTotal.add(Float.parseFloat(GB));
        }

        Float total = resTotal.stream().reduce(Float::sum).get();

        String formatTotal = String.format("%.2f", total);
        String formatUsed = String.format("%.2f", used);

        HardDiskMessage hardDiskMessage = new HardDiskMessage();
        hardDiskMessage.setServerId(serverMessage.getServerId());
        hardDiskMessage.setHardDiskTime(new Date());
        hardDiskMessage.setHardDiskUsed(formatUsed);
        hardDiskMessage.setHardDiskUsable(String.format("%.2f", total - used));

        int effectCount = hardDiskMessageMapper.insertSelective(hardDiskMessage);

        if (effectCount == IConnectServiceImpl.DATABASE_ERROR) {
            throw new DatabaseException("数据库异常");
        }
    }

    /**
     * 处理CPU信息
     * @param sshHelper
     * @param serverMessage
     */
    private void resolveCPU(SSHHelper sshHelper, ServerMessage serverMessage) {
        String s = sshHelper.execCommand(LinuxCommand.CPU_MESSAGE, SSHControl.UN_ENABLE_ENTER);

        Matcher matcher = PatternConfig.CPUCompile.matcher(s);

        List<Float> tmp = new ArrayList<>();

        while(matcher.find()) {
            tmp.add(Float.parseFloat(matcher.group()));
        }

        float used = 100 - tmp.get(3);

        CpuMessage cpuMessage = new CpuMessage();
        cpuMessage.setCpuTime(new Date());
        cpuMessage.setCpuUsed(String.format("%.2f", used));
        cpuMessage.setCpuUs(String.valueOf(tmp.get(0)));
        cpuMessage.setCpuSy(String.valueOf(tmp.get(1)));
        cpuMessage.setCpuNi(String.valueOf(tmp.get(2)));
        cpuMessage.setCpuWa(String.valueOf(tmp.get(4)));
        cpuMessage.setCpuHi(String.valueOf(tmp.get(5)));
        cpuMessage.setCpuSi(String.valueOf(tmp.get(6)));
        cpuMessage.setCpuSt(String.valueOf(tmp.get(7)));
        cpuMessage.setServerId(serverMessage.getServerId());

        int effectCount = cpuMessageMapper.insertSelective(cpuMessage);

        if (effectCount == IConnectServiceImpl.DATABASE_ERROR) {
            throw new DatabaseException("数据库异常");
        }
    }

    /**
     * 处理负载信息
     * @param sshHelper
     * @param serverMessage
     */
    private void resolveLoadavg(SSHHelper sshHelper, ServerMessage serverMessage) {
        String commandResult = sshHelper.execCommand(LinuxCommand.LOADAVG, SSHControl.UN_ENABLE_ENTER);
        String[] commands = commandResult.split(" ");

        LoadavgMessage loadavgMessage = new LoadavgMessage();
        loadavgMessage.setServerId(serverMessage.getServerId());
        loadavgMessage.setLoadavgTime(new Date());
        loadavgMessage.setLoadavgOne(commands[0]);
        loadavgMessage.setLoadavgFive(commands[1]);
        loadavgMessage.setLoadavgFifteen(commands[2]);

        int effectCount = loadavgMessageMapper.insertSelective(loadavgMessage);

        if (effectCount == IConnectServiceImpl.DATABASE_ERROR) {
            throw new DatabaseException("数据库异常");
        }
    }
}
