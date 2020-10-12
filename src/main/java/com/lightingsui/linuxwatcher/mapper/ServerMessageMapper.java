package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.ServerMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ServerMessageMapper {
    int deleteByPrimaryKey(Integer serverId);

    int insert(ServerMessage record);

    int insertSelective(ServerMessage record);

    ServerMessage selectByPrimaryKey(Integer serverId);

    int updateByPrimaryKeySelective(ServerMessage record);

    int updateByPrimaryKey(ServerMessage record);

    List<ServerMessage> selectAll();

    int selectCount(ServerMessage record);

    ServerMessage selectByCond(ServerMessage record);

    List<ServerMessage> selectAllOnlyServerId();

    int selectServerIdByHost(String host);

    String selectUserHexoLocaltion(String host);

}