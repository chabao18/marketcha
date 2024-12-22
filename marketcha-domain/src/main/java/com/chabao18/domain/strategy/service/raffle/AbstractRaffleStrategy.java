package com.chabao18.domain.strategy.service.raffle;

import com.chabao18.domain.strategy.model.entity.RaffleAwardEntity;
import com.chabao18.domain.strategy.model.entity.RaffleFactorEntity;
import com.chabao18.domain.strategy.model.entity.RuleActionEntity;
import com.chabao18.domain.strategy.model.entity.StrategyEntity;
import com.chabao18.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.chabao18.domain.strategy.repository.IStrategyRepository;
import com.chabao18.domain.strategy.service.IRaffleStrategy;
import com.chabao18.domain.strategy.service.armory.IStrategyDispatch;
import com.chabao18.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.chabao18.types.enums.ResponseCode;
import com.chabao18.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractRaffleStrategy implements IRaffleStrategy {
    protected IStrategyRepository repository;
    protected IStrategyDispatch strategyDispatch;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // params validation
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        StrategyEntity strategy = repository.queryStrategyEntityByStrategyId(strategyId);

        // begin rule-filter
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = this.doCheckRaffleBeforeLogic(RaffleFactorEntity.builder().userId(userId).strategyId(strategyId).build(), strategy.ruleModels());
        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())) {
            if (DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionEntity.getRuleModel())) {
                // rule-blacklist
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionEntity.getData().getAwardId())
                        .build();
            } else if (DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode().equals(ruleActionEntity.getRuleModel())) {
                // rule-weight
                RuleActionEntity.RaffleBeforeEntity raffleBeforeEntity = ruleActionEntity.getData();
                String ruleWeightValueKey = raffleBeforeEntity.getRuleWeightValueKey();
                Integer awardId = strategyDispatch.getRandomAwardId(strategyId, ruleWeightValueKey);
                return RaffleAwardEntity.builder()
                        .awardId(awardId)
                        .build();
            }
        }

        // default: no rules
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();

    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);

}
