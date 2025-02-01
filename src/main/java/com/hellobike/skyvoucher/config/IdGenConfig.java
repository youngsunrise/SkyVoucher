package com.hellobike.skyvoucher.config;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class IdGenConfig {
    public void idGenInit() throws ParseException {
        //创建设置对象，设置特殊工作id(每个应用需要使用不同workerid)
        short id = 6;
        IdGeneratorOptions options = new IdGeneratorOptions(id);
        //设置
        options.SeqBitLength = 6;
        //设置基准时间
        String dateString = "2025-01-01 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(dateString);
        options.BaseTime = date.getTime();
        YitIdHelper.setIdGenerator(options);
    }
}
