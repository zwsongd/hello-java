package com.enjoy.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnjoyGlue extends IJobHandler {
    private Logger logger = LoggerFactory.getLogger(EnjoyGlue.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        logger.info("测试GLUE任务，我已被执行");
        return SUCCESS;
    }
}
