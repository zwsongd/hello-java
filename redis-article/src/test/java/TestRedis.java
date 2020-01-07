import com.james.cache.utils.JedisUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Redis业务测试用例
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRedis {



	@Resource
	private JedisUtils jedis;

	@Test
	public void testHomework() {
		// 生成已有的四个订单
		Map<String, String> orderMap1 = new HashMap();
		orderMap1.put("orderId", "1");
		orderMap1.put("money", "36.6");
		orderMap1.put("time", "2018-01-01");

		Map<String, String> orderMap2 = new HashMap();
		orderMap2.put("orderId", "2");
		orderMap2.put("money", "38.6");
		orderMap2.put("time", "2018-01-01");

		Map<String, String> orderMap3 = new HashMap();
		orderMap3.put("orderId", "3");
		orderMap3.put("money", "39.6");
		orderMap3.put("time", "2018-01-01");

		// 放redis中
		jedis.hmset("order:1",orderMap1);
		jedis.hmset("order:2",orderMap2);
		jedis.hmset("order:3",orderMap3);

		// 把订单信息的key放到队列
		jedis.lpush("user:1:order", "order:1", "order:2", "order:3");

		// 新产生了一个订单order:4
		Map<String, String> orderMap4 = new HashMap();
		orderMap4.put("orderId", "4");
		orderMap4.put("money", "40.6");
		orderMap4.put("time", "2018-01-01");

		jedis.hmset("order:4",orderMap4);

		// 追加一个order:4放入队列第一个位置
		jedis.lpush("user:1:order", "order:4");

		// 查询用户订单记录
		List<String> orderKeys = jedis.lrange("user:1:order", 0, -1);
		for(String orderKey:orderKeys){
			Map<String, String> stringStringMap = jedis.hgetAll(orderKey);
			List<String> order = jedis.hmget(orderKey,"orderId","money","time");
			System.out.println(order);
		}
		jedis.close();
	}

}