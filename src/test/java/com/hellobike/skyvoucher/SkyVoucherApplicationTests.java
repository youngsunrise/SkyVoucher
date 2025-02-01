package com.hellobike.skyvoucher;

import com.github.yitter.idgen.YitIdHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SkyVoucherApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testIdGenerator() {
        System.out.println(YitIdHelper.nextId());
    }

}
