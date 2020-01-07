package com.quartz.config;

import com.quartz.job.BusinessJob;
import com.quartz.job.XXXService;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.*;

/**
 * Created by Peter on 11/15 015.
 */
@Configuration
public class QuartzConfig {

    /**
     * 耦合业务
     * 将调度环境信息传递给业务方法，如：调度时间，批次等
     * @return
     */
    @Bean(name = "businessJobDetail")
    public JobDetailFactoryBean businessJobDetail() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(BusinessJob.class);//业务bean是多例的还是单例的？

        //将参数封装传递给job
        JobDataMap jobDataMap =new JobDataMap();
        jobDataMap.put("time",System.currentTimeMillis());
        jobDetailFactoryBean.setJobDataAsMap(jobDataMap);
        return  jobDetailFactoryBean;
    }

    /**
     * 普通业务类
     * @param serviceBean
     * @return
     */
    @Bean(name = "serviceBeanDetail")
    public MethodInvokingJobDetailFactoryBean serviceBeanDetail(XXXService serviceBean) {//业务bean，单例的
        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
        // 是否并发执行
        jobDetail.setConcurrent(false);
        // 需要执行的实体bean
        jobDetail.setTargetObject(serviceBean);
        // 需要执行的方法
        jobDetail.setTargetMethod("business");
        return jobDetail;
    }

    // 简单触发器
    @Bean(name = "simpleTrigger")
    public SimpleTriggerFactoryBean simpleTrigger(JobDetail businessJobDetail) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(businessJobDetail);
        // 设置任务启动延迟
        trigger.setStartDelay(0);
        // 每5秒执行一次
        trigger.setRepeatInterval(3000);
        return trigger;
    }

    //cron触发器
    @Bean(name = "cronTrigger")
    public CronTriggerFactoryBean cronTrigger(JobDetail serviceBeanDetail) {
        CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
        triggerFactoryBean.setJobDetail(serviceBeanDetail);
        triggerFactoryBean.setCronExpression("0/6 * * * * ?");
        return triggerFactoryBean;
    }

    /**
     * 调度工厂,将所有的触发器引入
     * @return
     */
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactory(Trigger simpleTrigger, Trigger cronTrigger) {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        // 延时启动，应用启动1秒后
        bean.setStartupDelay(1);
        // 注册触发器
        bean.setTriggers(simpleTrigger,cronTrigger);
        return bean;
    }
}
