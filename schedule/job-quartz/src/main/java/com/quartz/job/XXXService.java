package com.quartz.job;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class XXXService {
    public void business(){
        System.out.println("业务方法执行...." + ",线程号："+Thread.currentThread().getName());
    }

}