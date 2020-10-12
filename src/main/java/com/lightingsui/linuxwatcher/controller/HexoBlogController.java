package com.lightingsui.linuxwatcher.controller;

import com.lightingsui.linuxwatcher.common.CommonResult;
import com.lightingsui.linuxwatcher.model.ServerMessage;
import com.lightingsui.linuxwatcher.service.IHexoBlogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * hexo
 *
 * @author ：隋亮亮
 * @since ：2020/10/11 11:56
 */
@RestController
@RequestMapping("hexo")
@Api(tags = "HexoBlogController", description = "hexo")
public class HexoBlogController {
    @Autowired
    private IHexoBlogService hexoBlogService;

    @ApiOperation("查询hexo文件位置")
    @RequestMapping(value = "hexo-folder-localtion", method = RequestMethod.GET)
    public CommonResult<String> getHexoFolderLocation(HttpServletRequest request) {
        return hexoBlogService.getHexoFolderLocation((ServerMessage)request.getSession().getAttribute("connect"));
    }

    @ApiOperation("设置hexo文件位置")
    @RequestMapping(value = "set-hexo-folder", method = RequestMethod.GET)
    public CommonResult<Boolean> setHexoFolder(@ApiParam("hexo文件夹位置") @RequestParam String hexoFolderLocation, HttpServletRequest request) {
        return hexoBlogService.setHexoFolder(hexoFolderLocation, (ServerMessage)request.getSession().getAttribute("connect"));
    }

    @ApiOperation("查看hexo模板内容")
    @RequestMapping(value = "hexo-template", method = RequestMethod.GET)
    public CommonResult<String> getHexoTemplate(HttpServletRequest request) {
        return hexoBlogService.getHexoTemplate((ServerMessage)request.getSession().getAttribute("connect"));
    }

    @ApiOperation("设置hexo模板")
    @RequestMapping(value = "set-hexo-template", method = RequestMethod.POST)
    public CommonResult<Boolean> setHexoTemplate(String hexoTemplate, HttpServletRequest request) {
        return hexoBlogService.setHexoTemplate(hexoTemplate, (ServerMessage)request.getSession().getAttribute("connect"));
    }

    @ApiOperation("上传博客")
    @RequestMapping(value = "upload-blog", method = RequestMethod.POST)
    public CommonResult<Boolean> uploadBlog(String content, String fileName, HttpServletRequest request) {
        return hexoBlogService.uploadBlog((ServerMessage)request.getSession().getAttribute("connect"), content, fileName);
    }
}
