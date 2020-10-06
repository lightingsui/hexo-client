package com.lightingsui.linuxwatcher.utils;

import java.text.DecimalFormat;

/**
 * 容量转换工具
 *
 * @author ：隋亮亮
 * @since ：2020/10/4 21:07
 */
public class ConversionOfNumberSystems {
    public final static String TO_GB = "GB";
    public final static String TO_MB = "MB";
    public final static String TO_KB = "KB";

    public static final boolean ENABLE_UNIT = true;
    public static final boolean UN_ENABLE_UNIT = false;

    /**
     * 将 {@code byte} 转化为 {@code KB} 或 {@code MB} 或 {@code GB}
     * @param byteNum 待转化的 {@code byte}，字符串{@link String} 格式
     * @param type 转化的类型，即 {@link #TO_GB}，{@link #TO_MB}, {@link #TO_KB}
     * @return 转化后的结果
     * @since 1.0
     */
    public static String byteConverseOther(String byteNum, String type, boolean enableUnit) {
        return byteConverseOther(Long.parseLong(byteNum), type, enableUnit);
    }

    /**
     * 将 {@code byte} 转化为 {@code KB} 或 {@code MB} 或 {@code GB}
     * @param size 待转化的 {@code byte}
     * @param type 转化的类型，即 {@link #TO_GB}，{@link #TO_MB}, {@link #TO_KB}
     * @return 转化后的结果
     * @since 1.0
     */
    public static String byteConverseOther(long size, String type, boolean enableUnit) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("0.00");
        if (TO_GB.equals(type)) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i));
            if(enableUnit) {
                bytes.append("GB");
            }
        }
        else if (TO_MB.equals(type)) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i));
            if(enableUnit) {
                bytes.append("MB");
            }
        }
        else if (TO_KB.equals(type)) {
            double i = (size / (1024.0));
            bytes.append(format.format(i));

            if(enableUnit) {
                bytes.append("KB");
            }
        }
        else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0");
            }
            else {
                bytes.append((int) size);
            }

            if(enableUnit) {
                bytes.append("B");
            }
        }
        return bytes.toString();
    }

    /**
     * 将 {@code GB} 或 {@code KB} 或者 {@code MB} 转化为 {@code Bytes}
     * @param pattern 待转换的字符串
     * @return 转化后的{@code byte}
     * @since 1.0
     */
    public static Long OtherToBytes(String pattern) {
        int len = pattern.length();
        char c = pattern.charAt(len - 1);

        StringBuilder stringBuilder = new StringBuilder(pattern);
        stringBuilder.delete(len - 1, len);

        Long value = 0L;
        if(stringBuilder.length() != 0) {
            value = Long.parseLong(stringBuilder.toString());
        }

        switch (c) {
            case 'b':
            case 'B':
                return value;
            case 'k':
            case 'K':
                return value * 1024;
            case 'm':
            case 'M':
                return value * 1024 * 1024;
            case 'g':
            case 'G':
                return value * 1024 * 1024 * 1024;
            default:
                // fall through
                return value;
        }
    }
}
