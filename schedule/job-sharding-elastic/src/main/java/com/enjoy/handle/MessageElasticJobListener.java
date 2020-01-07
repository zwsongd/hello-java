package com.enjoy.handle;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作业监听器
 */
public class MessageElasticJobListener implements ElasticJobListener {

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String msg = date + " 【任务开始执行-" + shardingContexts.getJobName() + "】";
        System.out.println("给管理发邮件："+msg);

    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
    	String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String msg = date + " 【任务执行结束-" + shardingContexts.getJobName() + "】" ;
        System.out.println("给管理发邮件："+msg);
    }

}