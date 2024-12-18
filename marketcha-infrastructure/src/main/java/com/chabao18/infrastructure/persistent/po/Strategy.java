package com.chabao18.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class Strategy {
    private String id;
    private String strategyId;
    private String strategyDesc;
    private String ruleModels;
    private Date createTime;
    private Date updateTime;
}
