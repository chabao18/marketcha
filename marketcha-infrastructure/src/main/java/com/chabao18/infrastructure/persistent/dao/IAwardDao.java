package com.chabao18.infrastructure.persistent.dao;

import com.chabao18.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IAwardDao {
    List<Award> queryAwardList();
}
