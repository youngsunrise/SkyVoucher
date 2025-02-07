package com.hellobike.skyvoucher.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yitter.idgen.YitIdHelper;
import com.hellobike.skyvoucher.dto.Result;
import com.hellobike.skyvoucher.entity.UserVoucher;
import com.hellobike.skyvoucher.entity.Voucher;
import com.hellobike.skyvoucher.entity.VoucherSeckill;
import com.hellobike.skyvoucher.mapper.VoucherMapper;
import com.hellobike.skyvoucher.mapper.VoucherSeckillMapper;
import com.hellobike.skyvoucher.service.UserVoucherService;
import com.hellobike.skyvoucher.mapper.UserVoucherMapper;
import com.hellobike.skyvoucher.service.VoucherSeckillService;
import com.hellobike.skyvoucher.utils.RedisConsts;
import com.hellobike.skyvoucher.utils.SimpleRedisLock;
import com.hellobike.skyvoucher.utils.VoucherStatus;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
* @author young
* @description 针对表【tb_user_voucher】的数据库操作Service实现
* @createDate 2025-02-01 14:44:30
*/
@Service
public class UserVoucherServiceImpl extends ServiceImpl<UserVoucherMapper, UserVoucher>
    implements UserVoucherService{

}




