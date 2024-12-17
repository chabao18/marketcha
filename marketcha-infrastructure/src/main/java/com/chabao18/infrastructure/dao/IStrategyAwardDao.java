package com.chabao18.infrastructure.dao;

import com.chabao18.infrastructure.dao.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyAwardDao {
    List<StrategyAward> queryStrategyAwardList();
}
