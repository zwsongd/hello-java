package com.quartz.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BusinessJob implements Job{
    int i = 0;

    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String name = dataMap.get("time").toString();
        business(name);
	}

    //重型任务，1000W数据统计，把任务敲碎 -- E-job
    private void business(String time){
        //
        i++;
        System.out.println("BusinessJob start --- time:"+time+", thread:" + Thread.currentThread().getName() );
    }

}