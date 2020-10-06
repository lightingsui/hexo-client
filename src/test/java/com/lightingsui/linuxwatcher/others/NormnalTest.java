package com.lightingsui.linuxwatcher.others;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.lightingsui.linuxwatcher.command.LinuxCommand;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.ssh.SSHControl;
import com.lightingsui.linuxwatcher.ssh.SSHHelper;
import com.lightingsui.linuxwatcher.utils.ConversionOfNumberSystems;
import org.junit.Test;

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
        System.out.println(ConversionOfNumberSystems.byteConverseOther(size, ConversionOfNumberSystems.TO_GB));
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


            System.out.println(ConversionOfNumberSystems.byteConverseOther(r1, ConversionOfNumberSystems.TO_KB) + "      " +  ConversionOfNumberSystems.byteConverseOther(r2, ConversionOfNumberSystems.TO_KB));
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

        String s = ConversionOfNumberSystems.byteConverseOther(aLong, ConversionOfNumberSystems.TO_KB);
        System.out.println(s);
    }

}
