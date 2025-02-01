package com.hellobike.skyvoucher.req;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AddSecKillVoucherReq {

    private Long id;

    private String name;

    /**
     *
     */
    private BigDecimal discount;

    /**
     * 1.普通券 2.限量券
     * com.hellobike.skyvoucher.utils.VoucherType
     */
    private Integer type;

    /**
     * 1.启用 2.禁用
     * com.hellobike.skyvoucher.utils.VoucherStatus
     */
    private Integer status;

    /**
     *
     */
    private Integer stock;

    /**
     *
     */
    private LocalDateTime beginTime;

    /**
     *
     */
    private LocalDateTime endTime;
}
