package com.lightingsui.linuxwatcher.config;

import java.util.regex.Pattern;

/**
 * 正则表达式配置
 *
 * @author ：隋亮亮
 * @since ：2020/10/7 9:57
 */
public class PatternConfig {

    public static Pattern compile;
    public static Pattern hardDiskCompile;
    public static Pattern CPUCompile;

    static {
        compile = Pattern.compile("\\d+");
        hardDiskCompile = Pattern.compile("\\d+(G|M|K|B)");
        CPUCompile = Pattern.compile("\\d+[.]\\d+");
    }
}
