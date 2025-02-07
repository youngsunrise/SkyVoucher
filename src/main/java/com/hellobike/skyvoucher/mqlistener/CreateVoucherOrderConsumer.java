package com.hellobike.skyvoucher.mqlistener;

import com.alibaba.fastjson.JSON;
import com.hellobike.skyvoucher.dto.Result;
import com.hellobike.skyvoucher.dto.UserVoucherDTO;
import com.hellobike.skyvoucher.service.VoucherSeckillService;
import com.hellobike.skyvoucher.utils.RedisConsts;
import com.hellobike.skyvoucher.utils.RocketMqConsts;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RocketMQMessageListener(
        topic = RocketMqConsts.SECKILL_TOPIC,
        selectorType = SelectorType.TAG,
        selectorExpression = RocketMqConsts.GET_VOUCHER_TAG,
        consumerGroup = RocketMqConsts.SECKILL_CONSUMER_GROUP
)
public class CreateVoucherOrderConsumer implements RocketMQListener<Message> {
    @Autowired
    private VoucherSeckillService voucherSeckillService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(Message message) {
        String msg = new String(message.getBody());
        UserVoucherDTO dto = JSON.parseObject(msg, UserVoucherDTO.class);
        Long uniqueId = dto.getId();
        Long userId = dto.getUserId();
        Long voucherId = dto.getVoucherId();
        String consumed = RedisConsts.MSG_CONSUME_MARK + userId + ":" + voucherId;
        //避免重复消费，幂等校验
        if (stringRedisTemplate.opsForValue().get(consumed) != null) {
            log.info("消息已被成功消费！", userId, voucherId);
        } else {
            //调用下单业务逻辑
            //因为voucherSeckillService为注入的Bean，实际上是Spring代理的对象，因此事务不会失效
            Result result = voucherSeckillService.createUserVoucher(uniqueId, userId, voucherId);
            if (Boolean.FALSE.equals(result.getSuccess())) {
                log.info("异步领券出现异常，msg:{}, 错误信息:{}", dto, result.getErrorMsg());
                //手动回滚redis
                //回滚库存
                stringRedisTemplate.opsForValue().increment(RedisConsts.SEC_KILL_STOCK_KEY + voucherId, 1);
                //删除领取记录
                stringRedisTemplate.opsForValue().getAndDelete(RedisConsts.USER_VOUCHER_RECORD
                        + userId + ":" + voucherId);
            } else {
                stringRedisTemplate.opsForValue().set(consumed, "1");
                log.info("用户id:{}，领取了券id:{},", userId, voucherId);
            }
        }
    }
}
