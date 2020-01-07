package com.enjoy.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.enjoy.business.EnjoyBusiness;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

//@ElasticJobConf(name = "EnjoyDataflowJob",cron = "0/5 * * * * ?"
//		,shardingItemParameters = "0=beijing,1=shanghai",shardingTotalCount = 2
//		,listener = "com.enjoy.handle.MessageElasticJobListener",jobExceptionHandler = "com.enjoy.handle.CustomJobExceptionHandler"
//)
public class EnjoyDataflowJob implements DataflowJob<String> {
	@Autowired
	private EnjoyBusiness enjoyBusiness;

	@Override
	public List fetchData(ShardingContext context) {
		String sql = enjoyBusiness.getSql(context.getShardingParameter());
		return Arrays.asList(sql);
	}

	@Override
	public void processData(ShardingContext context, List<String> list) {
		System.out.println("EnjoyDataflowJob,当前分片："+context.getShardingParameter());
		enjoyBusiness.process(list.get(0));
	}
}