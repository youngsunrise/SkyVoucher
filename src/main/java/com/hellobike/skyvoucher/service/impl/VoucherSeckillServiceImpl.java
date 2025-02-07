package com.hellobike.skyvoucher.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yitter.idgen.YitIdHelper;
import com.hellobike.skyvoucher.dto.Result;
import com.hellobike.skyvoucher.dto.UserVoucherDTO;
import com.hellobike.skyvoucher.entity.UserVoucher;
import com.hellobike.skyvoucher.entity.Voucher;
import com.hellobike.skyvoucher.entity.VoucherSeckill;
import com.hellobike.skyvoucher.mapper.UserVoucherMapper;
import com.hellobike.skyvoucher.mapper.VoucherMapper;
import com.hellobike.skyvoucher.mqproducer.CreateVoucherOrderProducer;
import com.hellobike.skyvoucher.req.AddSecKillVoucherReq;
import com.hellobike.skyvoucher.service.VoucherSeckillService;
import com.hellobike.skyvoucher.mapper.VoucherSeckillMapper;
import com.hellobike.skyvoucher.utils.*;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
* @author young
* @description 针对表【tb_voucher_seckill】的数据库操作Service实现
* @createDate 2025-02-01 14:47:00
*/
@Service
public class VoucherSeckillServiceImpl extends ServiceImpl<VoucherSeckillMapper, VoucherSeckill>
    implements VoucherSeckillService{

    @Autowired
    VoucherMapper voucherMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserVoucherMapper userVoucherMapper;
    @Autowired
    CreateVoucherOrderProducer createVoucherOrderProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addSecKillVoucher(AddSecKillVoucherReq secKillVoucherReq) {
        Voucher voucher = new Voucher();
        voucher.setCreateTime(LocalDateTime.now());
        voucher.setUpdateTime(LocalDateTime.now());
        VoucherSeckill voucherSeckill = new VoucherSeckill();
        BeanUtils.copyProperties(secKillVoucherReq, voucher);
        BeanUtils.copyProperties(secKillVoucherReq,voucherSeckill);
        System.out.println(voucher.getName());
        if (voucherMapper.insert(voucher) == 1) {
            try {
                //voucher表主键id自增，voucherMapper.insert(voucher)成功后会更新这里的voucher对应的id字段
                voucherSeckill.setId(voucher.getId());
                voucherSeckill.setUpdateTime(LocalDateTime.now());
                if (save(voucherSeckill)) {
                    //添加券成功，库存同时写入redis，提高响应速度
                    stringRedisTemplate.opsForValue()
                            .set(RedisConsts.SEC_KILL_STOCK_KEY + voucher.getId(), voucherSeckill.getStock().toString());
//                    int a = 2 / 0;//测试异常
                    return Result.ok("添加秒杀券成功");
                }
            } catch (Exception e) {
                //出现异常就需要手动回滚redis操作
                System.out.println("添加秒杀券异常，redis操作回滚");
                stringRedisTemplate.opsForValue().getAndDelete(RedisConsts.SEC_KILL_STOCK_KEY + voucher.getId());
                //再次抛出确保数据库回滚
                throw new RuntimeException("添加秒杀券异常，数据库回滚", e);
            }
        }
        return Result.fail("添加秒杀券失败");
    }

    /**
     * 领券的外部逻辑，通过分布式锁确保一人一单；库存校验避免超卖
     * 通过redis预热库存等信息，提高响应速度；同时借助mq异步完成数据库更新确保最终一致性
     */
    @Override
    public Result getSecKillVoucher(Long userId, Long voucherId) {
        //引入自定义的分布式锁，保证一人一单，一个用户，同一时间只有一个线程为其执行业务
        SimpleRedisLock simpleRedisLock = new SimpleRedisLock(RedisConsts.SECKILL_LOCK + userId, stringRedisTemplate);
        //尝试获取锁
        Boolean getLock = simpleRedisLock.tryLock(5);
        if (!getLock) {
            //竞争失败线程直接返回
            return Result.fail("抢券失败");
        }


        try {
            //获取锁线程执行业务逻辑
            //获取当前ServiceImpl的代理对象，防止this.method调用方式使得事务方法失效
//            VoucherSeckillServiceImpl proxy = (VoucherSeckillServiceImpl) AopContext.currentProxy();
//            return proxy.createUserVoucher(userId, voucherId);


            //redis一人一单校验
            if (stringRedisTemplate.opsForValue().get(RedisConsts.USER_VOUCHER_RECORD +
                    userId.toString() + ":" + voucherId.toString()) != null) {
                return Result.fail("用户已经领取了该券！");
            }
            //redis库存校验
            String stockKey = RedisConsts.SEC_KILL_STOCK_KEY + voucherId;
            if (null == stringRedisTemplate.opsForValue().get(stockKey)) {
                return Result.fail("未找到redis券库存信息！");
            }
            if (Long.valueOf(stringRedisTemplate.opsForValue().get(stockKey)) > 0) {
                //扣减库存
                stringRedisTemplate.opsForValue().decrement(RedisConsts.SEC_KILL_STOCK_KEY + voucherId, 1);
                //唯一ID生成器
                long nextId = YitIdHelper.nextId();
                //抢券后记录在redis
                stringRedisTemplate.opsForValue().set(RedisConsts.USER_VOUCHER_RECORD
                        + userId + ":" + voucherId, nextId + "", 24 * 7, TimeUnit.HOURS);
                //异步更新
                UserVoucherDTO userVoucherDTO = new UserVoucherDTO(nextId, userId, voucherId);
                //发送rocketmq实现异步下单
                createVoucherOrderProducer.sendAsyncMsg(JSON.toJSONString(userVoucherDTO), RocketMqConsts.GET_VOUCHER_TAG);
                return Result.ok(nextId);
            } else {
                return Result.fail("库存不足！");
            }

        } finally {
            simpleRedisLock.unlock();
        }

    }

    /**
     * 本方法内部的校验基于数据库，在异步更新时被调用
     */
    @Override
    @Transactional
    public Result createUserVoucher (Long uniqueId, Long userId, Long voucherId) {

        //1.检查券状况，时间，库存
        Voucher voucher = voucherMapper.selectById(voucherId);
        VoucherSeckill voucherSeckill = query().eq("v_id", voucherId).one();
        if (voucher.getStatus().equals(VoucherStatus.VOUCHER_DISABLED.getCode())) {
            return Result.fail("券被禁用");
        }
        if (voucherSeckill.getBeginTime().isAfter(LocalDateTime.now()) ||
                voucherSeckill.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("秒杀已结束");
        }

        //2.检查用户资格，是否已抢过改券
        long count = userVoucherMapper.selectCount(Wrappers.lambdaQuery(UserVoucher.class)
                .eq(UserVoucher::getVoucherId, voucherId)
                .eq(UserVoucher::getUserId, userId));
        if (count > 0) {
            return Result.fail("不能重复抢券");
        }

        //3.乐观锁更新库存
        Boolean success = update()
                .setSql("v_stock = v_stock - 1")
                .eq("v_id", voucherId)
                .gt("v_stock", 0)
                .set("v_update_time", LocalDateTime.now())
                .update();
        if (!success) {
            return Result.fail("库存不足");
        }

        //4.用户抢券成功，创建 用户-券 关联记录
        UserVoucher userVoucherRecord = new UserVoucher();
        userVoucherRecord.setVoucherId(voucherId);
        userVoucherRecord.setId(uniqueId);
        userVoucherRecord.setUserId(userId);
        userVoucherRecord.setStatus(UserVoucherStatus.UNUSED.getCode());
        int insert = userVoucherMapper.insert(userVoucherRecord);
        if (insert == 1) {
            return Result.ok(userVoucherRecord);
        }
        return Result.fail("抢券失败");
    }
}




