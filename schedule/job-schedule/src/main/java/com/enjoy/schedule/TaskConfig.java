package com.enjoy.schedule;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author songzw
 * @date 11/15 015
 */
//@Component
//@EnableScheduling
public class TaskConfig {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * 单机
     */
//    @Scheduled(fixedDelayString = "5000")
    public void getTask1() {
        //竞争锁逻辑代码 .....


        System.out.println("任务1，当前时间：" + dateFormat.format(new Date())+",线程号："+Thread.currentThread().getName());
        throw new RuntimeException("xxxxx");
    }

//    @Scheduled(cron = "0/5 * *  * * ?")
    public void getTask2() {
        System.out.println("任务2，当前时间：" + dateFormat.format(new Date())+",线程号："+Thread.currentThread().getName());
    }

}
