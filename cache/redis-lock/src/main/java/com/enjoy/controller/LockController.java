package com.enjoy.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

@Api(value = "锁机制", description = "锁机制说明")
@RestController
public class LockController {
    private static long count = 20;//黄牛
    private CountDownLatch countDownLatch = new CountDownLatch(5);

    @Resource(name="redisLock")
    private Lock lock;

	@ApiOperation(value="售票")
    @RequestMapping(value = "/sale", method = RequestMethod.GET)
    public Long sale() throws InterruptedException {
        count = 20;
        countDownLatch = new CountDownLatch(5);

        System.out.println("-------共20张票，分五个窗口开售-------");
        new PlusThread().start();
        new PlusThread().start();
        new PlusThread().start();
        new PlusThread().start();
        new PlusThread().start();
        return count;
    }

    // 线程类模拟一个窗口买火车票
    public class PlusThread extends Thread {
        private int amount = 0;//抢多少张票

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "开始售票");
            countDownLatch.countDown();
            if (countDownLatch.getCount()==0){
                System.out.println("----------售票结果------------------------------");
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (count > 0) {
                lock.lock();
                try {
                    if (count > 0) {
                        //模拟卖票业务处理
                        amount++;
                        count--;
                    }
                }finally{
                    lock.unlock();
                }

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + "售出"+ (amount) + "张票");
        }
    }
}