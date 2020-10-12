package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.Hexo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HexoMapper {
    int deleteByPrimaryKey(Integer hexoId);

    int insert(Hexo record);

    int insertSelective(Hexo record);

    Hexo selectByPrimaryKey(Integer hexoId);

    int updateByPrimaryKeySelective(Hexo record);

    int updateByPrimaryKeyWithBLOBs(Hexo record);

    int updateByPrimaryKey(Hexo record);

    List<Hexo> selectAll();

    int selectCount(Hexo record);

    List<Hexo> selectByCond(Hexo record);

    String selectUserTemplate(String host);

    void updateByServerId(int serverId, String hexoTemplate);
}