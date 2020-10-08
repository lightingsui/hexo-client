package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.HardDiskMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface HardDiskMessageMapper {
    int deleteByPrimaryKey(Integer hardDiskId);

    int insert(HardDiskMessage record);

    int insertSelective(HardDiskMessage record);

    HardDiskMessage selectByPrimaryKey(Integer hardDiskId);

    int updateByPrimaryKeySelective(HardDiskMessage record);

    int updateByPrimaryKey(HardDiskMessage record);

    List<HardDiskMessage> selectAll();

    int selectCount(HardDiskMessage record);

    List<HardDiskMessage> selectByCond(HardDiskMessage record);

    Date selectHardDiskStartTime(int serverId);

    List<HardDiskMessage> selectRecentlyMessage(int serverId, int recentlyTime);

    List<HardDiskMessage> selectAssignMessage(int serverId, String beginDate, String endDate);
}