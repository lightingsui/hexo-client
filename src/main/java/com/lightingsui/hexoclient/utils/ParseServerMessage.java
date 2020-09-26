package com.lightingsui.hexoclient.utils;

import com.lightingsui.hexoclient.model.ServerMessageUname;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析 {@code uname} 的信息
 *
 * @author ：隋亮亮
 * @since ：2020/9/23 15:04
 */
public class ParseServerMessage {
    private static Pattern compile;

    static {
        compile = Pattern.compile("(.+)(20[0-9]{2})");
    }

    public static ServerMessageUname parse(String message) {
        ServerMessageUname messageUname = new ServerMessageUname();

        String[] param = message.split(" ", 4);

        messageUname.setKernelName(param[0]);
        messageUname.setNodeName(param[1]);
        messageUname.setKernelRelease(param[2]);

        Matcher group = compile.matcher(param[3]);
        group.find();

        try {
            int end = group.end();
            String kernelVersion = group.group();

            messageUname.setKernelVersion(kernelVersion);

            String messageSubString = param[3].substring(end + 1);
            String[] subParam = messageSubString.split(" ");

            messageUname.setMachine(subParam[0]);
            messageUname.setProcessor(subParam[1]);
            messageUname.setHardwarePlatform(subParam[2]);
            messageUname.setOperatingSystem(subParam[3]);

            return messageUname;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void main(String[] args) {
        ServerMessageUname parse = parse("Linux lighting 5.4.0-47-generic #51-Ubuntu SMP Fri Sep 4 19:50:52 UTC 2020 x86_64 x86_64 x86_64 GNU/Linux");
        System.out.println();
    }

}
