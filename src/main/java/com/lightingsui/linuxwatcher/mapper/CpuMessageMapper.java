package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.CpuMessage;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

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
}