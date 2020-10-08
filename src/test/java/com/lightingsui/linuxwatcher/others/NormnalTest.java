package com.lightingsui.linuxwatcher.others;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.lightingsui.linuxwatcher.command.LinuxCommand;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.pojo.CpuMessage;
import com.lightingsui.linuxwatcher.ssh.SSHControl;
import com.lightingsui.linuxwatcher.ssh.SSHHelper;
import com.lightingsui.linuxwatcher.utils.ConversionOfNumberSystems;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：隋亮亮
 * @since ：2020/10/4 20:11
 */
public class NormnalTest {
    @Test
    public void test01() {
        String s = "Mem:        1883492      279764       78928         476     1524800     1404732";

        Pattern compile = Pattern.compile("\\d+");
        Matcher matcher = compile.matcher(s);

        while (matcher.find()) {
            String group = matcher.group();

            System.out.println(group);
        }

        System.out.println();
    }

    @Test
    public void test02() {
        String s = "Disk /dev/vda: 42.9 GB, 42949672960 bytes, 83886080 sectors\n" +
                "Disk label type: dos\n" +
                "Disk identifier: 0x0008d73a\n" +
                "You have mail in /var/spool/mail/root";

        Pattern compile = Pattern.compile("\\d+ (字节|bytes)");

        Matcher matcher = compile.matcher(s);

        long size = 0L;
        while(matcher.find()) {
            String group = matcher.group().split(" ")[0];
            System.out.println(group);

            size += Long.parseLong(group);
        }


        System.out.println("=================");
        System.out.println(ConversionOfNumberSystems.byteConverseOther(size, ConversionOfNumberSystems.TO_GB, ConversionOfNumberSystems.ENABLE_UNIT));
    }

    @Test
    public void test03() {
        String s = "530372.81";
        System.out.println(Math.round(Float.parseFloat(s)));
        DateTime dateTime = DateUtil.offsetSecond(DateUtil.date(), -530250);

        System.out.println(dateTime.toString());

        String s1 = DateUtil.formatBetween(530250 * 1000);
        System.out.println(s1);
    }

    @Test
    public void test04() {
        String s = "Tasks:  79 total,   1 running,  77 sleeping,   0 stopped,   1 zombie";

        Pattern compile = Pattern.compile("\\d+");
        Matcher matcher = compile.matcher(s);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void test05() {
        String s = "  66B    118B ";
        s = s.trim();

        String[] s1 = s.split(" ");
        String in = s1[0];
        String out = s1[s1.length - 1];


        System.out.println(in + "    " + out);
    }

    @Test
    public void test06() {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setHost("39.106.81.183");
        serverMessage.setPassword("Aa13404420573");
        serverMessage.setPort(22);

        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getPassword());
        sshHelper.getConnection();

        while(true) {
            String s = sshHelper.execCommand("dstat  1 2 |tail -n +5 | cut -d\"|\" -f 3", SSHControl.ENABLE_ENTER);


            String res = s.split("\n")[0];
            res = res.trim();
            String[] s1 = res.split(" ");

            Long r1 = ConversionOfNumberSystems.OtherToBytes(s1[0]);
            Long r2 = ConversionOfNumberSystems.OtherToBytes(s1[s1.length - 1]);


            System.out.println(ConversionOfNumberSystems.byteConverseOther(r1, ConversionOfNumberSystems.TO_KB, ConversionOfNumberSystems.ENABLE_UNIT) + "      " +
                    ConversionOfNumberSystems.byteConverseOther(r2, ConversionOfNumberSystems.TO_KB, ConversionOfNumberSystems.ENABLE_UNIT));
        }
    }

    @Test
    public void test07() {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setHost("172.21.18.202");
        serverMessage.setPassword("suiroot");
        serverMessage.setPort(22);

        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getPassword());
        sshHelper.getConnection();

        String s = sshHelper.execCommand(LinuxCommand.CHECK_INSTALL_DSTAT, SSHControl.ENABLE_ENTER);

        System.out.println(s);
    }

    @Test
    public void test08() {
        Long aLong = ConversionOfNumberSystems.OtherToBytes("12021B");

        System.out.println(aLong);

        String s = ConversionOfNumberSystems.byteConverseOther(aLong, ConversionOfNumberSystems.TO_KB, ConversionOfNumberSystems.ENABLE_UNIT);
        System.out.println(s);
    }

    @Test
    public void test09() {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setHost("39.106.81.183");
        serverMessage.setPassword("Aa13404420573");
        serverMessage.setPort(22);

        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getPassword());
        sshHelper.getConnection();

        String memoryTotal = sshHelper.execCommand(LinuxCommand.MEMORY_MESSAGE, SSHControl.UN_ENABLE_ENTER);
        String swapTotal = sshHelper.execCommand(LinuxCommand.MEMORY_MESSAGE_SWAP, SSHControl.UN_ENABLE_ENTER);

        Pattern compile = Pattern.compile("\\d+");
        Matcher matcher = compile.matcher(memoryTotal);
        List<Integer> list = new ArrayList<>();

        while(matcher.find()) {
            list.add(Integer.parseInt(matcher.group()));
        }

        Matcher matcher1 = compile.matcher(swapTotal);
        List<Integer> list1 = new ArrayList<>();

        while(matcher1.find()) {
            list1.add(Integer.parseInt(matcher1.group()));
        }

        int used = list.get(1) + list1.get(1);
        int usable = list.get(5) + list1.get(2);


        System.out.println("已用：" + list.get(1) + "   可用：" + list.get(5));
        System.out.println("已用：" + list1.get(1) + "   可用：" + list1.get(2));
    }

    @Test
    public void test10() {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setHost("39.106.81.183");
        serverMessage.setPassword("Aa13404420573");
        serverMessage.setPort(22);

        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getPassword());
        sshHelper.getConnection();

        String hardDiskUsed = sshHelper.execCommand(LinuxCommand.HARD_DISK_USED, SSHControl.ENABLE_ENTER);
        String hardDiskTotal = sshHelper.execCommand(LinuxCommand.HARD_DISK_TOTAL_UPGRADE, SSHControl.ENABLE_ENTER);


        // 获取使用量
        List<Float> res = new ArrayList<>();
        Pattern compile = Pattern.compile("\\d+(G|M|K|B)");
        Matcher matcher = compile.matcher(hardDiskUsed);

        while(matcher.find()) {
            String group = matcher.group();
            Long tmp = ConversionOfNumberSystems.OtherToBytes(group);
            String GB = ConversionOfNumberSystems.byteConverseOther(tmp, ConversionOfNumberSystems.TO_GB, ConversionOfNumberSystems.UN_ENABLE_UNIT);

            res.add(Float.parseFloat(GB));
        }

        Float used = res.stream().reduce(Float::sum).get();


        // 获取总量
        List<Float> resTotal = new ArrayList<>();
        Matcher totalMatcher = compile.matcher(hardDiskTotal);

        while(totalMatcher.find()) {
            String group = totalMatcher.group();
            Long tmp = ConversionOfNumberSystems.OtherToBytes(group);
            String GB = ConversionOfNumberSystems.byteConverseOther(tmp, ConversionOfNumberSystems.TO_GB, ConversionOfNumberSystems.UN_ENABLE_UNIT);

            resTotal.add(Float.parseFloat(GB));
        }

        Float total = resTotal.stream().reduce(Float::sum).get();

        String formatTotal = String.format("%.2f", total);
        String formatUsed = String.format("%.2f", used);


        System.out.println(formatTotal);
        System.out.println(formatUsed);
    }



    @Test
    public void test11() {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setHost("172.21.18.202");
        serverMessage.setPassword("suiroot");
        serverMessage.setPort(22);

        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getPassword());
        sshHelper.getConnection();
        String s = sshHelper.execCommand(LinuxCommand.PROCESS, SSHControl.UN_ENABLE_ENTER);

        System.out.println(s);


    }


    @Test
    public void test12() {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setHost("172.21.18.202");
        serverMessage.setPassword("suiroot");
        serverMessage.setPort(22);

        SSHHelper sshHelper = new SSHHelper(serverMessage.getHost(), serverMessage.getPassword());
        sshHelper.getConnection();
        String s = sshHelper.execCommand(LinuxCommand.CPU_MESSAGE, SSHControl.UN_ENABLE_ENTER);

        Matcher matcher = Pattern.compile("\\d+[.]\\d+").matcher(s);

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


        System.out.println();
    }

}
