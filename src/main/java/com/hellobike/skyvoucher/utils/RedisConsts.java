package com.hellobike.skyvoucher.utils;

public class RedisConsts {
    //使用时拼接券id
    public static final String SEC_KILL_STOCK_KEY = "seckill:key:";
    //使用时拼接lockName
    public static final String REDIX_SETNX_LOCK_PREFIX = "simple:redis:lock:";
    //秒杀业务锁名
    public static final String SECKILL_LOCK = "seckill:";
}
