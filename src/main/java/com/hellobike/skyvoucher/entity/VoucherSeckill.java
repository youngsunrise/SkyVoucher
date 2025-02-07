package com.hellobike.skyvoucher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName tb_voucher_seckill
 */
@TableName(value ="tb_voucher_seckill")
@Data
public class VoucherSeckill implements Serializable {
    /**
     * 
     */
    @TableId(value = "v_id")
    private Long id;

    /**
     * 
     */
    @TableField(value = "v_stock")
    private Integer stock;

    /**
     * 
     */
    @TableField(value = "v_begin_time")
    private LocalDateTime beginTime;

    /**
     * 
     */
    @TableField(value = "v_end_time")
    private LocalDateTime endTime;

    /**
     * 
     */
    @TableField(value = "v_update_time")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}