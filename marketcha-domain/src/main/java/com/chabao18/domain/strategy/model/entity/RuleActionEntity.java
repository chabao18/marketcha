package com.chabao18.domain.strategy.model.entity;

import com.chabao18.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {
    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();
    private String ruleModel;
    private T data;


    static public class RaffleEntity {}

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleBeforeEntity extends RaffleEntity {
        private Long strategyId;
        private String ruleWeightValueKey;
        private Integer awardId;
    }

    static public class RaffleCenterEntity extends RaffleEntity {

    }

    static public class RaffleAfterEntity extends RaffleEntity {

    }


}
