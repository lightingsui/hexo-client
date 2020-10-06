package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.NetworkMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface NetworkMessageMapper {
    int deleteByPrimaryKey(Integer networkId);

    int insert(NetworkMessage record);

    int insertSelective(NetworkMessage record);

    NetworkMessage selectByPrimaryKey(Integer networkId);

    int updateByPrimaryKeySelective(NetworkMessage record);

    int updateByPrimaryKey(NetworkMessage record);

    List<NetworkMessage> selectAll();

    int selectCount(NetworkMessage record);

    List<NetworkMessage> selectByCond(NetworkMessage record);

    Date selectNetworkStartTime(int serverId);

    List<NetworkMessage> selectRecentlyMessage(int serverId, int recentlyTime);

    List<NetworkMessage> selectAssignMessage(int serverId, String beginDate, String endDate);
}