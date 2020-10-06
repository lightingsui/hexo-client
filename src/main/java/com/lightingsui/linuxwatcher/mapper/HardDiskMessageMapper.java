package com.lightingsui.linuxwatcher.mapper;

import com.lightingsui.linuxwatcher.pojo.HardDiskMessage;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

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
}