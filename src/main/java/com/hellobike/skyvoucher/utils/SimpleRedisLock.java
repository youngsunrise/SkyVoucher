package com.hellobike.skyvoucher.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleRedisLock {
    //利用redis的set nx ex命令实现的简单分布式锁
    private String lockName;
    private StringRedisTemplate stringRedisTemplate;
    private static final String ID_PREFIX = UUID.randomUUID().toString();

    public boolean tryLock(long timeOutSec) {
        //准备线程标识，用于标注锁持有者
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        //尝试获取锁
        //参数分别为 key(锁名), value(线程表示), 超时释放时间， 时间单位
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(RedisConsts.REDIX_SETNX_LOCK_PREFIX + lockName,
                        threadId, timeOutSec, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(success)) {
            return true;
        }
        return false;
    }

    public void unlock() {
        //判断线程标识后，释放锁
        String mark = stringRedisTemplate.opsForValue().get(RedisConsts.REDIX_SETNX_LOCK_PREFIX + lockName);
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        //判断当前线程标识，与当前锁持有者标识是否一致
        if (mark.equals(threadId)) {
            stringRedisTemplate.delete(RedisConsts.REDIX_SETNX_LOCK_PREFIX + lockName);
        }
    }
}
