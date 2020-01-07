package com.enjoy.business;

import org.springframework.stereotype.Service;

@Service
public class EnjoyBusiness {

	private String sql_tpl = "select * from xxx where param = %s";

	public String getSql(String param){
		return String.format(sql_tpl,param);
	}

	/**
	 * index指示，现在跑的是第几片
	 * total指示，总共有几片
     */
	public void process(String sql) {

		//分片任务
		System.out.println("当前执行sql："+sql);
	}

}