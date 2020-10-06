package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.MemoryMessage;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemoryMessageMapper {
    int deleteByPrimaryKey(Integer memoryId);

    int insert(MemoryMessage record);

    int insertSelective(MemoryMessage record);

    MemoryMessage selectByPrimaryKey(Integer memoryId);

    int updateByPrimaryKeySelective(MemoryMessage record);

    int updateByPrimaryKey(MemoryMessage record);

    List<MemoryMessage> selectAll();

    int selectCount(MemoryMessage record);

    List<MemoryMessage> selectByCond(MemoryMessage record);
}