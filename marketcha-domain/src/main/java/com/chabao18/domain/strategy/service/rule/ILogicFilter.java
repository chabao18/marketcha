package com.chabao18.domain.strategy.service.rule;

import com.chabao18.domain.strategy.model.entity.RuleActionEntity;
import com.chabao18.domain.strategy.model.entity.RuleMatterEntity;

public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);

}
