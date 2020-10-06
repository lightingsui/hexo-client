package com.lightingsui.linuxwatcher;

import com.lighting.annotation.*;

/**
 * MybatisGenerator启动类
 *
 * @author ：隋亮亮
 * @since ：2020/10/4 17:56
 */
@MybatisGeneratorRepackaging
@EnableSwaggerComments
@EnableExtend
@LombokData
@EnableXmlOverwrite
public class MyBatisGeneratorRun {
    public static void main(String[] args) {
        com.lighting.run.MybatisGeneratorRepackaging.run(MyBatisGeneratorRun.class);
    }
}
