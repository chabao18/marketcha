package com.chabao18.domain.strategy.service;

import com.chabao18.domain.strategy.model.entity.RaffleAwardEntity;
import com.chabao18.domain.strategy.model.entity.RaffleFactorEntity;

public interface IRaffleStrategy {
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);

}
