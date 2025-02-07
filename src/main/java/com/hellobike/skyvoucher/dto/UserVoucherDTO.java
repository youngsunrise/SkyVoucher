package com.hellobike.skyvoucher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVoucherDTO {
    //唯一id雪花算法生成
    private Long id;
    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private Long voucherId;

}
