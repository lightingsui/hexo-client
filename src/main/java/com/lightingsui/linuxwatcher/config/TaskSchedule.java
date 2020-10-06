package com.lightingsui.linuxwatcher.config;

import com.lightingsui.linuxwatcher.command.LinuxCommand;
import com.lightingsui.linuxwatcher.mapper.NetworkMessageMapper;
import com.lightingsui.linuxwatcher.mapper.ServerMessageMapper;
import com.lightingsui.linuxwatcher.pojo.NetworkMessage;
import com.lightingsui.linuxwatcher.pojo.ServerMessage;
import com.lightingsui.linuxwatcher.ssh.SSHControl;
import com.lightingsui.linuxwatcher.ssh.SSHHelper;
import com.lightingsui.linuxwatcher.utils.ConversionOfNumberSystems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时监测任务
 *
 * @author ：隋亮亮
 * @since ：2020/10/6 8:37
 */
//@Component
public class TaskSchedule {
    public static final List<ServerMessage> TASK = new ArrayList<>();
    private static final int RUN_INTERVAL = 5000;

    private boolean once = true;

    @Autowired
    private ServerMessageMapper serverMessageMapper;
    @Autowired
    private NetworkMessageMapper networkMessageMapper;


    @Scheduled(fixedDelay = RUN_INTERVAL)
    public void taskRun() {
        if (once) {
            // 启动时先查询出所有服务器信息
            TASK.addAll(serverMessageMapper.selectAllOnlyServerId());

            once = false;
        } else {

            System.out.println("我是定时任务" + System.currentTimeMillis());

            for (int i = 0; i < TASK.size(); i++) {
                ServerMessage serverMessage = TASK.get(i);
                SSHHelper sshHelper = new SSHHelper(serverMessage.getServerIp(), serverMessage.getServerPassword(),
                        Integer.parseInt(serverMessage.getServerPort()));

                sshHelper.getConnection();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String s = sshHelper.execCommand(LinuxCommand.GET_SYSTEM_NETWORK_SPEED, SSHControl.ENABLE_ENTER);

                        // 格式化命令输出
                        String res = s.split("\n")[0];
                        res = res.trim();
                        String[] s1 = res.split(" ");

                        Long r1 = ConversionOfNumberSystems.OtherToBytes(s1[0]);
                        Long r2 = ConversionOfNumberSystems.OtherToBytes(s1[s1.length - 1]);

                        String out = ConversionOfNumberSystems.byteConverseOther(r1, ConversionOfNumberSystems.TO_KB);
                        String in = ConversionOfNumberSystems.byteConverseOther(r2, ConversionOfNumberSystems.TO_KB);


                        // 存储
                        NetworkMessage networkMessage = new NetworkMessage();
                        networkMessage.setNetworkInSpeed(in);
                        networkMessage.setNetworkOutSpeed(out);
                        networkMessage.setNetworkTime(new Date());
                        networkMessage.setServerId(serverMessage.getServerId());

                        networkMessageMapper.insertSelective(networkMessage);
                    }
                };

                ThreadPoolConfig.executor.execute(runnable);
            }
        }
    }
}
