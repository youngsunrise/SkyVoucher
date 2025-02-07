package com.hellobike.skyvoucher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName tb_voucher
 */
@TableName(value ="tb_voucher")
@Data
public class Voucher implements Serializable {
    /**
     * 
     */
    @TableId(value = "v_id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "v_name")
    private String name;

    /**
     * 
     */
    @TableField(value = "v_discount")
    private BigDecimal discount;

    /**
     * 1.普通券 2.限量券
     */
    @TableField(value = "v_type")
    private Integer type;

    /**
     * 1.启用 2.禁用
     */
    @TableField(value = "v_status")
    private Integer status;

    /**
     * 
     */
    @TableField(value = "v_create_time")
    private LocalDateTime createTime;

    /**
     * 
     */
    @TableField(value = "v_update_time")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}