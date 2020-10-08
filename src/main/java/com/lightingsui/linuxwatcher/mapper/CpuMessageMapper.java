package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.CpuMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface CpuMessageMapper {
    int deleteByPrimaryKey(Integer cpuId);

    int insert(CpuMessage record);

    int insertSelective(CpuMessage record);

    CpuMessage selectByPrimaryKey(Integer cpuId);

    int updateByPrimaryKeySelective(CpuMessage record);

    int updateByPrimaryKey(CpuMessage record);

    List<CpuMessage> selectAll();

    int selectCount(CpuMessage record);

    List<CpuMessage> selectByCond(CpuMessage record);

    List<CpuMessage> selectRecentlyMessage(int serverId, int recentlyTime);

    Date selectCpuStartTime(int serverId);

    List<CpuMessage> selectAssignMessage(int serverId, String beginDate, String endDate);
}