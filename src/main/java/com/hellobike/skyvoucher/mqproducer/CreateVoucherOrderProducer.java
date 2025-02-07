package com.hellobike.skyvoucher.mqproducer;

import com.alibaba.fastjson.JSON;
import com.hellobike.skyvoucher.dto.UserVoucherDTO;
import com.hellobike.skyvoucher.utils.RedisConsts;
import com.hellobike.skyvoucher.utils.RocketMqConsts;
import com.hellobike.skyvoucher.utils.TimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CreateVoucherOrderProducer {
    //消息发送超时
    @Value("${rocketmq.producer.send-message-timeout}")
    private Integer sendMsgTimeOut;
    //消费主体、一般来说一个消费者组里的消费者，只消费一个主题，避免rocketmq负载均衡出现问题
    private static final String topic = RocketMqConsts.SECKILL_TOPIC;
    //springAMQP的rocketMQ实现
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
//    public CreateVoucherOrderProducer (RocketMQTemplate rocketMQTemplate, DefaultMQProducer defaultMQProducer) {
//        this.rocketMQTemplate = rocketMQTemplate;
//        this.defaultMQProducer = defaultMQProducer;
//    }

    /**
     * 发送同步消息的方法，该方法会阻塞当前线程，直到收到broker消息代理的响应
     */
    public SendResult sendSyncMsg(String msgBody, String tag) {
//        //1.topic 2.tag 3.msgBody 4.编码
//        Message message = new Message(topic, tag, msgBody.getBytes(StandardCharsets.UTF_8));
        //1.destination（topic + tag拼接） 2.消息体
        SendResult sendResult = rocketMQTemplate.syncSend(
                topic + ":" + tag,
                MessageBuilder.withPayload(msgBody).build());
        log.info(": 消息体:{}，发送结果为:{}", msgBody, JSON.toJSONString(sendResult));
        return sendResult;
    }
    /**
     * 发送异步消息，线程无需阻塞等待，通过callback回调函数处理成功或失败的逻辑
     */
    public void sendAsyncMsg(String msgBody, String tag) {
        //1.destination（topic + tag拼接） 2.消息体 3.回调函数
        rocketMQTemplate.asyncSend(
                topic + ":" + tag,
                MessageBuilder.withPayload(msgBody).build(),
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info(": 消息发送成功！{}", msgBody);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error(": 消息{}发送失败！{}", msgBody, throwable.getLocalizedMessage());
                        //手动回滚redis
                        UserVoucherDTO dto = JSON.parseObject(msgBody, UserVoucherDTO.class);
                        //回滚库存
                        stringRedisTemplate.opsForValue().increment(RedisConsts.SEC_KILL_STOCK_KEY + dto.getVoucherId(), 1);
                        //删除领取记录
                        stringRedisTemplate.opsForValue().getAndDelete(RedisConsts.USER_VOUCHER_RECORD
                                + dto.getUserId() + ":" + dto.getUserId());
                    }
                });
    }


}
