package com.hellobike.skyvoucher.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VoucherStatus {
    VOUCHER_ENABLED(1, "启用"),
    VOUCHER_DISABLED(2, "禁用");

    private final int code; // 枚举代码值
    private final String description; // 枚举描述

    // 根据代码值解析枚举类型
    public static VoucherType parseByCode(int code) {
        for (VoucherType type : VoucherType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的优惠券状态代码: " + code);
    }
}
