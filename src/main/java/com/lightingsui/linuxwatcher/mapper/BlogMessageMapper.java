package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.BlogMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogMessageMapper {
    int deleteByPrimaryKey(Integer blogId);

    int insert(BlogMessage record);

    int insertSelective(BlogMessage record);

    BlogMessage selectByPrimaryKey(Integer blogId);

    int updateByPrimaryKeySelective(BlogMessage record);

    int updateByPrimaryKey(BlogMessage record);

    List<BlogMessage> selectAll();

    int selectCount(BlogMessage record);

    List<BlogMessage> selectByCond(BlogMessage record);
}