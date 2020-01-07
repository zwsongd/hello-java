package com.enjoy.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.enjoy.business.EnjoyBusiness;
import org.springframework.beans.factory.annotation.Autowired;

@ElasticJobConf(name = "EnjoySimpleJob",cron = "0/5 * * * * ?"
		,shardingItemParameters = "0=beijing|shenzhen|tianjin,1=shanghai",shardingTotalCount = 2
		,listener = "com.enjoy.handle.MessageElasticJobListener"
		,jobExceptionHandler = "com.enjoy.handle.CustomJobExceptionHandler"
)
public class EnjoySimpleJob implements SimpleJob {
	@Autowired
	private EnjoyBusiness enjoyBusiness;

	@Override
	public void execute(ShardingContext context) {
		System.out.println("EnjoySimpleJob,当前分片："+context.getShardingParameter());

		//当前起始
		//context.getShardingParameter(),回返切片信息beijing
		String sql = enjoyBusiness.getSql(context.getShardingParameter());
		enjoyBusiness.process(sql);
	}

}