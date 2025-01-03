package com.chabao18.domain.strategy.service.raffle;

import com.chabao18.domain.strategy.model.entity.RaffleAwardEntity;
import com.chabao18.domain.strategy.model.entity.RaffleFactorEntity;
import com.chabao18.domain.strategy.model.entity.RuleActionEntity;
import com.chabao18.domain.strategy.model.entity.StrategyEntity;
import com.chabao18.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.chabao18.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import com.chabao18.domain.strategy.repository.IStrategyRepository;
import com.chabao18.domain.strategy.service.IRaffleStrategy;
import com.chabao18.domain.strategy.service.armory.IStrategyDispatch;
import com.chabao18.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.chabao18.types.enums.ResponseCode;
import com.chabao18.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
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

        // 抽奖前的过滤规则
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

        // 无规则则走默认
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);

        // 过滤抽奖中、抽奖后的规则
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModelVO(strategyId, awardId);

        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionCenterEntity = this.doCheckRaffleCenterLogic(RaffleFactorEntity.builder()
                .userId(userId)
                .strategyId(strategyId)
                .awardId(awardId)
                .build(), strategyAwardRuleModelVO.raffleCenterRuleModelList());

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionCenterEntity.getCode())){
            log.info("【临时日志】中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。");
            return RaffleAwardEntity.builder()
                    .awardDesc("中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();

    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics);


}
