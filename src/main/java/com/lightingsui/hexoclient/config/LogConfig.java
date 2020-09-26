package com.lightingsui.hexoclient.config;

import org.apache.log4j.Logger;

/**
 * 日志配置
 *
 * @author ：隋亮亮
 * @since ：2020/9/23 16:12
 */
public class LogConfig {
    public final static Logger LOGGER = Logger.getLogger(LogConfig.class);

    public static final String ERROR = "error";
    public static final String INFO = "info";

    public static void log(String message, String type) {
        switch (type) {
            case ERROR:
                LOGGER.error(message);
                break;
            case INFO:
                LOGGER.info(message);
                break;
            default:
                // fall through
        }
    }
}
