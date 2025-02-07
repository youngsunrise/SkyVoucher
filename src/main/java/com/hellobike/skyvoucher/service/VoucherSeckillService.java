package com.hellobike.skyvoucher.service;

import com.hellobike.skyvoucher.dto.Result;
import com.hellobike.skyvoucher.entity.VoucherSeckill;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hellobike.skyvoucher.req.AddSecKillVoucherReq;

/**
* @author young
* @description 针对表【tb_voucher_seckill】的数据库操作Service
* @createDate 2025-02-01 14:47:00
*/
public interface VoucherSeckillService extends IService<VoucherSeckill> {
    Result addSecKillVoucher(AddSecKillVoucherReq secKillVoucherReq);

    Result getSecKillVoucher(Long userId, Long voucherId);

    Result createUserVoucher (Long uniqueId, Long userId, Long voucherId);
}
