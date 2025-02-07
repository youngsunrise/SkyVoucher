package com.hellobike.skyvoucher.utils;

public class RedisConsts {
    //使用时拼接券id
    public static final String SEC_KILL_STOCK_KEY = "seckill:key:";
    //使用时拼接lockName
    public static final String REDIX_SETNX_LOCK_PREFIX = "simple:redis:lock:";
    //秒杀业务锁名
    public static final String SECKILL_LOCK = "seckill:";
    //用户-券 记录，用于库存一人一单校验
    public static final String USER_VOUCHER_RECORD = "user:voucher:record:";

    //mq幂等校验
    public static final String MSG_CONSUME_MARK = "message:consume:";
}
