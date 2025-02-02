package com.hellobike.skyvoucher.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum UserVoucherStatus {
    UNUSED(1, "未使用"),
    USED(2, "已使用");

    private final int code; // 枚举代码值
    private final String description; // 枚举描述

    // 根据代码值解析枚举类型
    public static VoucherType parseByCode(int code) {
        for (VoucherType type : VoucherType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的用户优惠券状态代码: " + code);
    }
}
