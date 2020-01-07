package cn.enjoy.mg;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import cn.enjoy.entity.Comment;
import cn.enjoy.entity.User;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Update.PushOperatorBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



import com.mongodb.Block;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringQueryTest {

	private static final Logger logger = LoggerFactory
            .getLogger(SpringQueryTest.class);

	@Resource
	private MongoOperations tempelate;
	
	
	
	// -----------------------------操作符使用实例------------------------------------------

	// db.users.find({"username":{"$in":["lison", "mark", "james"]}}).pretty()
	// 查询姓名为lison、mark和james这个范围的人
	@Test
	public void testInOper() {
		Query query = query(where("username").in("lison", "mark", "james"));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
		
		
	}


	// db.users.find({"lenght":{"$exists":true}}).pretty()
	// 判断文档有没有关心的字段
	@Test
	public void testExistsOper() {

		Query query = query(where("lenght").exists(true));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
	
	}

	// db.users.find().sort({"username":1}).limit(1).skip(2)
	// 测试sort，limit，skip
	@Test
	public void testSLSOper() {
		
		//Query query = query(where(null)).with(new Sort(new Sort.Order(Direction.ASC, "username"))).limit(1).skip(2);
		Query query = query(where(null)).with(Sort.by(Direction.ASC, "username")).limit(1).skip(2);
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);

	}

	// db.users.find({"lenght":{"$not":{"$gte":1.77}}}).pretty()
	// 查询高度小于1.77或者没有身高的人
	// not语句 会把不包含查询语句字段的文档 也检索出来

	@Test
	public void testNotOper() {
		Query query = query(where("lenght").not().gte(1.77));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
		

	}
	
	// -----------------------------字符串数组查询实例------------------------------------------

	// db.users.find({"favorites.movies":"蜘蛛侠"})
	// 查询数组中包含"蜘蛛侠"
	@Test
	public void testArray1() {
		Query query = query(where("favorites.movies").is("蜘蛛侠"));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
	}

	// db.users.find({"favorites.movies":[ "妇联4","杀破狼2", "战狼", "雷神1","神奇动物在哪里"]},{"favorites.movies":1})
	// 查询数组等于[ “杀破狼2”, “战狼”, “雷神1” ]的文档，严格按照数量、顺序；

	@Test
	public void testArray2() {
		Query query = query(where("favorites.movies").is(Arrays.asList("妇联4","杀破狼2", "战狼", "雷神1","神奇动物在哪里")));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
	}


	//数组多元素查询
	@Test
	public void testArray3() {
		// db.users.find({"favorites.movies":{"$all":[ "雷神1", "战狼"]}},{"favorites.movies":1})
		// 查询数组包含["雷神1", "战狼" ]的文档，跟顺序无关
		
		Query query = query(where("favorites.movies").all(Arrays.asList("雷神1", "战狼")));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
		
		
//		db.users.find({"favorites.movies":{"$in":[ "雷神1", "战狼" ]}},{"favorites.movies":1})
//		查询数组包含[“雷神1”, “战狼” ]中任意一个的文档，跟顺序无关，跟数量无关
		 query = query(where("favorites.movies").in(Arrays.asList("雷神1", "战狼")));
		 find = tempelate.find(query, User.class);
		 printUsers(find);
	}

	// // db.users.find({"favorites.movies.0":"妇联4"},{"favorites.movies":1})
	// 查询数组中第一个为"妇联4"的文档

	@Test
	public void testArray4() {
		Query query = query(where("favorites.movies.0").is("妇联4"));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
	}

	// db.users.find({},{"favorites.movies":{"$slice":[1,2]},"favorites":1})
	// $slice可以取两个元素数组,分别表示跳过和限制的条数；

	@Test
	public void testArray5() {
		Query query = query(where(null));
		query.fields().include("favorites").slice("favorites.movies", 1, 2);
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
	}

	// -----------------------------对象数组查询实例------------------------------------------
	
	
	//db.users.find({"comments":{"author":"lison6","content":"lison评论6","commentTime":ISODate("2017-06-06T00:00:00Z")}})
	//备注：对象数组精确查找
	//坑：居然和属性定义的顺序有关
	@Test
	public void testObjArray1() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date commentDate = formatter.parse("2017-06-06 08:00:00");
		Comment comment = new Comment();
		comment.setAuthor("lison6");
		comment.setCommentTime(commentDate);
		comment.setContent("lison评论6");

		Query query = query(where("comments").is(comment));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
	}


	//数组多元素查询
	@Test
	public void testObjArray2() {
		
		
		
//		查找lison1 或者 lison12评论过的user （$in查找符） 
//		db.users.find({"comments.author":{"$in":["lison1","lison12"]}}).pretty()
//		  备注：跟数量无关，跟顺序无关；

		Query query = query(where("comments.author").in(Arrays.asList("lison1","lison12")));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
		
		
//		查找lison1 和 lison12都评论过的user
//		db.users.find({"comments.author":{"$all":["lison12","lison1"]}}).pretty()
//		 备注：跟数量有关，跟顺序无关；

		query = query(where("comments.author").all(Arrays.asList("lison1","lison12")));
		find = tempelate.find(query, User.class);
		printUsers(find);
	}
	
	
	
	@Test
	//(1)注意相关的实体bean要加上注解@document，@dbRef
	//(2)spring对dbRef进行了封装，发起了两次查询请求
	public void dbRefTest(){
		System.out.println("----------------------------");
		List<User> users = tempelate.findAll(User.class);
		System.out.println("----------------------------");
		System.out.println(users);
//		System.out.println(users.get(0).getComments());
	}

	
	
	
	private void printUsers(List<User> find) {
		for (User user : find) {
			System.out.println(user);
		}
		System.out.println(find.size());
	}
	
	
	//---------------------------------------------------------

	
	//查找lison5评语为包含“苍老师”关键字的user（$elemMatch查找符） 
//	db.users.find({"comments":{"$elemMatch":{"author" : "lison5", "content" : { "$regex" : ".*苍老师.*"}}}})
//备注：数组中对象数据要符合查询对象里面所有的字段，$全元素匹配，和顺序无关；

	@Test
	public void testObjArray3() throws ParseException {
//		and(where("author").is("lison5"),where("content").regex(".*苍老师.*")))
		Criteria andOperator = new Criteria().andOperator(where("author").is("lison5"),where("content").regex(".*苍老师.*"));
		Query query = query(where("comments").elemMatch(andOperator));
		List<User> find = tempelate.find(query, User.class);
		printUsers(find);
	}

}