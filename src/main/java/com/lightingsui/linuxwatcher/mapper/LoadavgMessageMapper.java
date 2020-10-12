package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.LoadavgMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface LoadavgMessageMapper {
    int deleteByPrimaryKey(Integer loadavgId);

    int insert(LoadavgMessage record);

    int insertSelective(LoadavgMessage record);

    LoadavgMessage selectByPrimaryKey(Integer loadavgId);

    int updateByPrimaryKeySelective(LoadavgMessage record);

    int updateByPrimaryKey(LoadavgMessage record);

    List<LoadavgMessage> selectAll();

    int selectCount(LoadavgMessage record);

    List<LoadavgMessage> selectByCond(LoadavgMessage record);

    Date selectLoadavgStartTime(int serverId);

    List<LoadavgMessage> selectRecentlyMessage(int serverId, int recentlyTime);

    List<LoadavgMessage> selectAssignMessage(int serverId, String beginDate, String endDate);
}