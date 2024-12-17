package com.chabao18.infrastructure.dao;

import com.chabao18.infrastructure.dao.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyDao {
    List<Strategy> queryStrategyList();
}
