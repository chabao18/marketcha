package com.chabao18.domain.strategy.service.rule.impl;

import com.chabao18.domain.strategy.model.entity.RuleActionEntity;
import com.chabao18.domain.strategy.model.entity.RuleMatterEntity;
import com.chabao18.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.chabao18.domain.strategy.repository.IStrategyRepository;
import com.chabao18.domain.strategy.service.annotation.LogicStrategy;
import com.chabao18.domain.strategy.service.rule.ILogicFilter;
import com.chabao18.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.chabao18.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {
    @Resource
    private IStrategyRepository repository;

    public Long userScore = 4500L;

    // rule-weight format: 4000:102,103,104,105 5000:120,103,104,105,106,107 ...
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());

        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if (analyticalValueGroup == null || analyticalValueGroup.isEmpty()) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        // find the appropriate award level
        Long nextValue = analyticalSortedKeys.stream()
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);

        if (nextValue != null) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(strategyId)
                            .ruleWeightValueKey(analyticalValueGroup.get(nextValue))
                            .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long, String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroups) {
            if (ruleValueKey == null || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }

            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }
        return ruleValueMap;
    }
}
