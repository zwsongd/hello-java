package com.enjoy.handle;

import com.dangdang.ddframe.job.executor.handler.JobExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义异常处理
 */
public class CustomJobExceptionHandler implements JobExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(CustomJobExceptionHandler.class);

    @Override
    public void handleException(String jobName, Throwable cause) {
        logger.error(String.format("Job '%s' exception occur in job processing", jobName), cause);
        System.out.println("给管理发邮件：【"+jobName+"】任务异常。" + cause.getMessage());
    }

}