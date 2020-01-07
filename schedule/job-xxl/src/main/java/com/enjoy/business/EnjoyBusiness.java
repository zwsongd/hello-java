package com.enjoy.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EnjoyBusiness {
	private Logger logger = LoggerFactory.getLogger(EnjoyBusiness.class);

	/**
	 * index指示，现在跑的是第几片
	 * total指示，总共有几片
     */
	public void process(int index,int total,String param) {
		if (total == 1){
			logger.info("当前执行全业务处理,参数："+param);
			return;
		}

		//分片任务
		if (index == 1 ){
			// select * from xxx limit 0,500
		} else {
			// select * from xxx limit 500,1000
		}

		logger.info("当前执行第 "+(index+1)+"/"+total+"片的数据,参数："+param);
	}

}