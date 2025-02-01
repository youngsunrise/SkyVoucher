package com.hellobike.skyvoucher.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public enum VoucherType {
    COMMON_VOUCHER(1, "普通优惠券"),
    SECKILL_VOUCHER(2, "秒杀优惠券");

    private final int code; // 枚举代码值
    private final String description; // 枚举描述

    // 构造函数
    VoucherType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据代码值解析枚举类型
    public static VoucherType parseByCode(int code) {
        for (VoucherType type : VoucherType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的优惠券类型代码: " + code);
    }

}
