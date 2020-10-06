package com.lightingsui.linuxwatcher.service.impl;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.ssh.SSHHelper;
import com.lightingsui.linuxwatcher.vo.ServerMessageVo;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author ：隋亮亮
 * @since ：2020/10/5 11:15
 */
public class IConnectServiceImplTest {

    @Test
    public void getServerMessage() {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setHost("172.21.18.202");
        serverMessage.setPassword("suiroot");
        serverMessage.setPort(22);

        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getPassword());


        ServerMessageVo serverMessageVo = new ServerMessageVo();

        IConnectServiceImpl connectService = new IConnectServiceImpl();
//        sshHelper.getConnection();

//        boolean memoryTotal = connectService.getMemoryTotal(sshHelper, serverMessageVo);


        CommonResult<ServerMessageVo> serverMessage1 = connectService.getServerMessage(serverMessage);

        System.out.println();
    }

    @Test
    public void test02() {
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            final int tmp = i;
            new Thread(){
                @Override
                public void run() {
                    System.out.println("当前线程 " + tmp);
                    latch.countDown();
                }
            }.start();
        }

        try {
            latch.await();
            System.out.println("main run");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}