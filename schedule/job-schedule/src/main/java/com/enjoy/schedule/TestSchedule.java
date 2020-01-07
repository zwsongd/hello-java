package com.enjoy.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author songzw
 * @desc TODO
 * @date 2019-12-30 14:46
 */
@Component
public class TestSchedule {
    @Scheduled(cron = "0/10 * * * * ?")
    public void taskSchdule1() throws InterruptedException {
        Thread t = Thread.currentThread();
        System.out.println("taskSchule1 "+ new Date().toLocaleString() +" ThreadID:"+ t.getId() +" "+t.getName());
        Thread.sleep(11000);
        System.out.println("taskSchule1 end "+ new Date().toLocaleString() +" ThreadID:"+ t.getId() +" "+t.getName());
    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void taskSchdule2(){
        Thread t = Thread.currentThread();
        System.out.println("taskSchule2 "+ new Date().toLocaleString() +" ThreadID:"+  t.getId() +" "+t.getName());
    }
}

