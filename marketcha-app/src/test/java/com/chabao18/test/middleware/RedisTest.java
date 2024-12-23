package com.chabao18.test.middleware;

import com.chabao18.domain.strategy.model.entity.StrategyAwardEntity;
import com.chabao18.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Resource
    private IRedisService redisService;
    @Test
    public void test_redisSync() {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, 123);
        map.put(2, 345);
        Map<Integer, Integer> rmap = redisService.getMap("test_sync_with_java");
        rmap.putAll(map);
    }

    @Test
    public void rest_redisKeyFormat() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "Jack");
        map.put("gender", "male");
        Map<String, String> rmap = redisService.getMap("test:student");
        rmap.putAll(map);

        map = new HashMap<>();
        map.put("name", "Rose");
        map.put("gender", "female");
        rmap = redisService.getMap("test:teacher");
        rmap.putAll(map);
    }

    @Test
    public void test_jsonSerialize() {
        StrategyAwardEntity entity = new StrategyAwardEntity();
        redisService.setValue("strategy:1", entity);

        String cachedJson = redisService.getValue("strategy:1");
        System.out.println(cachedJson);

    }
}
