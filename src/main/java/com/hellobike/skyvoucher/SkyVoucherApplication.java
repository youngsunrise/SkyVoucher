package com.hellobike.skyvoucher;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hellobike.skyvoucher.mapper")
public class SkyVoucherApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyVoucherApplication.class, args);
    }

}
