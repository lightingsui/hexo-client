package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.MemoryMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

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

    List<MemoryMessage> selectRecentlyMessage(int serverId, int recentlyTime);

    Date selectMemoryStartTime(int serverId);

    List<MemoryMessage> selectAssignMessage(int serverId, String beginDate, String endDate);
}