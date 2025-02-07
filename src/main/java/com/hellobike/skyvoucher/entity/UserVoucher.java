package com.hellobike.skyvoucher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName tb_user_voucher
 */
@TableName(value ="tb_user_voucher")
@Data
public class UserVoucher implements Serializable {
    /**
     * 唯一特殊id，算法生成
     */
    @TableId
    private Long id;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long voucherId;

    /**
     * 1.未使用 2.已使用
     * com.hellobike.skyvoucher.utils.UserVoucherStatus
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}