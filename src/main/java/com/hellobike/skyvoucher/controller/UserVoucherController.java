package com.hellobike.skyvoucher.controller;


import com.hellobike.skyvoucher.dto.Result;
import com.hellobike.skyvoucher.req.AddSecKillVoucherReq;
import com.hellobike.skyvoucher.service.UserVoucherService;
import com.hellobike.skyvoucher.service.VoucherSeckillService;
import com.hellobike.skyvoucher.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voucher-center")
public class UserVoucherController {


    @Autowired
    VoucherService voucherService;
    @Autowired
    VoucherSeckillService voucherSeckillService;

    //限时秒杀券领取入口
    @PostMapping("getVoucher/{user_id}/{voucher_id}")
    public Result getSecKillVoucher(@PathVariable("user_id") Long userId, @PathVariable("voucher_id") Long voucherId) throws InterruptedException {
        return voucherSeckillService.getSecKillVoucher(userId, voucherId);
    }



    //添加秒杀优惠劵接口
    @PostMapping("addSecKillVoucher")
    public Result addSecKillVoucher(@RequestBody AddSecKillVoucherReq secKillVoucherReq) {
        return voucherSeckillService.addSecKillVoucher(secKillVoucherReq);
    }

}
