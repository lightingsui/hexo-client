package com.lightingsui.linuxwatcher.command;

/**
 * 命令
 *
 * @author ：隋亮亮
 * @since ：2020/9/23 15:01
 */
public class LinuxCommand {
    public static final String TEST_CONNECT_COMMAND = "whoami";
    public static final String DEFAULT_USER = "root";
    public static final String UNAME = "uname -a";
    public static final String HEXO_CHECK = "hexo";
    public static final String HEXO_NEW_PAGE = "hexo new";
    public static final String HEXO_GENERATE_PAGE = "hexo g";
    public static final String HEXO_DEPLOY = "hexo d";
    public static final String CD = "cd";


    public static final String MEMORY_MESSAGE = "free -m | tail -n +2 | head -n 1";
    public static final String MEMORY_MESSAGE_SWAP = "free -m | tail -n +3 | head -n 1";

    public static final String HARD_DISK_TOTAL = "fdisk -l | grep Disk";


    public static  final String SYSTEM_STARTUP_SECOND_TIME = "cat /proc/uptime | cut -d\" \" -f1";

    public static final String CURRENT_ON_LINE_USER_COUNT = "w | tail -n +3 | cut -d\" \" -f1 | sort | uniq -c | wc -l";
    public static final String CURRENT_SYSTEM_USER_TOTAL = "cat /etc/passwd|grep bash|wc -l";

    public static final String SYSTEM_HOST_NAME = "uname -a | cut -d\" \" -f2";

    public static final String CHECK_INSTALL_DSTAT = "dstat --version";
    public static final String GET_SYSTEM_NETWORK_SPEED = "dstat  1 2 |tail -n +5 | cut -d\"|\" -f 3";
}
