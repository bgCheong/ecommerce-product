package com.example.productservice.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final RedisTemplate<String, String> redisTemplate;

    public Boolean acquireLock(String lockKey, long timeoutSeconds) {
        // SETNX 명령어와 동일한 기능, 키가 없을 때만 값을 설정
        return redisTemplate
                .opsForValue()
                .setIfAbsent(lockKey, "locked", Duration.ofSeconds(timeoutSeconds));
    }

    public void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}