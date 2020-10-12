package com.lightingsui.linuxwatcher.service;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;

/**
 * hexo
 *
 * @author ：隋亮亮
 * @since ：2020/10/11 11:57
 */
public interface IHexoBlogService {
    CommonResult<Boolean> setHexoFolder(String hexoFolderLocation, ServerMessage connect);

    CommonResult<String> getHexoFolderLocation(ServerMessage connect);

    CommonResult<String> getHexoTemplate(ServerMessage connect);

    CommonResult<Boolean> setHexoTemplate(String hexoTemplate, ServerMessage connect);

    CommonResult<Boolean> uploadBlog(ServerMessage connect, String content, String fileName);
}
