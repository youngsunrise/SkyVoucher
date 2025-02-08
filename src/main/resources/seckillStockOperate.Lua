--准备变量，便于理解
local voucherGainRecord = KEYS[1]
local voucherStockKey = KEYS[2]
local nextId = ARGV[1]

--判断用户是否以及领取
if (redis.call('get', voucherGainRecord) ~= false)
then
    return 1                        --用户已经领取过
end
--判断库存是否充足
if (redis.call('get', voucherStockKey) == nil)
then
    return 2                        --未找到库存信息
end
if (tonumber(redis.call('get', voucherStockKey)) <= 0)
then
    return 3                        --库存不足
end
--库存充足，扣减库存，标记用户领取记录
redis.call('decr', voucherStockKey)
redis.call('set', voucherGainRecord, nextId)
return 0