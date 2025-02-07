--判断锁持有者线程标识与当前线程是否一致--
if (redis.call('get', KEYS[1]) == ARGV[1]) then
    --锁为当前线程持有，释放锁
    return redis.call('del', KEYS[1])
end
--否则什么都不做
return