package cn.enjoy.mg;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Aggregates.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.bson.BSON;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.PushOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.operation.OrderBy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class JavaQueryTest {

	private static final Logger logger = LoggerFactory
            .getLogger(JavaQueryTest.class);

	private MongoDatabase db;

	private MongoCollection<Document> collection;

	private MongoCollection<Document> orderCollection;

	@Resource(name="mongo")
	private MongoClient client;

	@Before
	public void init() {
		db = client.getDatabase("lison");
		collection = db.getCollection("users");
		orderCollection = db.getCollection("ordersTest");
	}

	// -----------------------------操作符使用实例------------------------------------------

	// db.users.find({"username":{"$in":["lison", "mark", "james"]}}).pretty()
	// 查询姓名为lison、mark和james这个范围的人
	@Test
	public void testInOper() {
		Bson in = in("username", "lison", "mark", "james");
		FindIterable<Document> find = collection.find(in);
		printOperation(find);
	}

	// db.users.find({"lenght":{"$exists":true}}).pretty()
	// 判断文档有没有关心的字段
	@Test
	public void testExistsOper() {
		Bson exists = exists("lenght", true);
		FindIterable<Document> find = collection.find(exists);
		printOperation(find);
	}

	// db.users.find().sort({"username":1}).limit(1).skip(2)
	// 测试sort，limit，skip
	@Test
	public void testSLSOper() {
		Document sort = new Document("username", 1);
		FindIterable<Document> find = collection.find().sort(sort).limit(1).skip(2);
		printOperation(find);
	}

	// db.users.find({"lenght":{"$not":{"$gte":1.77}}}).pretty()
	// 查询高度小于1.77或者没有身高的人
	// not语句 会把不包含查询语句字段的文档 也检索出来

	@Test
	public void testNotOper() {
		Bson gte = gte("lenght", 1.77);
		Bson not = not(gte);
		FindIterable<Document> find = collection.find(not);
		printOperation(find);
	}

	// -----------------------------字符串数组查询实例------------------------------------------

	// db.users.find({"favorites.movies":"蜘蛛侠"})
	// 查询数组中包含"蜘蛛侠"
	@Test
	public void testArray1() {
		Bson eq = eq("favorites.movies", "蜘蛛侠");
		FindIterable<Document> find = collection.find(eq);
		printOperation(find);
	}

	// db.users.find({"favorites.movies":[ "妇联4","杀破狼2", "战狼", "雷神1","神奇动物在哪里"]},{"favorites.movies":1})
	// 查询数组等于[ “杀破狼2”, “战狼”, “雷神1” ]的文档，严格按照数量、顺序；

	@Test
	public void testArray2() {
		Bson eq = eq("favorites.movies", Arrays.asList("妇联4","杀破狼2", "战狼", "雷神1","神奇动物在哪里"));
		FindIterable<Document> find = collection.find(eq);
		printOperation(find);
	}


	//数组多元素查询
	@Test
	public void testArray3() {
		
		// db.users.find({"favorites.movies":{"$all":[ "雷神1", "战狼"]}},{"favorites.movies":1})
		// 查询数组包含["雷神1", "战狼" ]的文档，跟顺序无关
		Bson all = all("favorites.movies", Arrays.asList("雷神1", "战狼"));
		FindIterable<Document> find = collection.find(all);
		printOperation(find);		
//		db.users.find({"favorites.movies":{"$in":[ "雷神1", "战狼" ]}},{"favorites.movies":1})
//		查询数组包含[“雷神1”, “战狼” ]中任意一个的文档，跟顺序无关，跟数量无关
		Bson in = in("favorites.movies", Arrays.asList("雷神1", "战狼"));
		find = collection.find(in);
		printOperation(find);
	}

	// // db.users.find({"favorites.movies.0":"妇联4"},{"favorites.movies":1})
	// 查询数组中第一个为"妇联4"的文档

	@Test
	public void testArray4() {
		Bson eq = eq("favorites.movies.0", "妇联4");
		FindIterable<Document> find = collection.find(eq);
		printOperation(find);
	}

	// db.users.find({},{"favorites.movies":{"$slice":[1,2]},"favorites":1})
	// $slice可以取两个元素数组,分别表示跳过和限制的条数；

	@Test
	public void testArray5() {
		Bson slice = slice("favorites.movies", 1, 2);
		Bson include = include("favorites");
		Bson projection = fields(slice, include);
		FindIterable<Document> find = collection.find().projection(projection);
		printOperation(find);
	}

	// -----------------------------对象数组查询实例------------------------------------------
	
	
	//db.users.find({"comments":{"author":"lison6","content":"lison评论6","commentTime":ISODate("2017-06-06T00:00:00Z")}})
	//备注：对象数组精确查找
	@Test
	public void testObjArray1() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date commentDate = formatter.parse("2017-06-06 08:00:00");
		
		Document comment = new Document().append("author", "lison6")
				                         .append("content", "lison评论6")
				                         .append("commentTime", commentDate);
		Bson eq = eq("comments", comment);
		FindIterable<Document> find = collection.find(eq);
		printOperation(find);	}


	//数组多元素查询
	@Test
	public void testObjArray2() {
		
		
//		查找lison1 或者 lison12评论过的user （$in查找符） 
//		db.users.find({"comments.author":{"$in":["lison1","lison12"]}}).pretty()
//		  备注：跟数量无关，跟顺序无关；

		Bson in = in("comments.author", Arrays.asList("lison1","lison12"));
		FindIterable<Document> find = collection.find(in);
		printOperation(find);		
		
//		查找lison1 和 lison12都评论过的user
//		db.users.find({"comments.author":{"$all":["lison12","lison1"]}}).pretty()
//		 备注：跟数量有关，跟顺序无关；

		Bson all = all("comments.author", Arrays.asList("lison12","lison1"));
		find = collection.find(all);
		printOperation(find);	
	}
	
	
	
	//查找lison5评语为包含“苍老师”关键字的user（$elemMatch查找符） 
//	db.users.find({"comments":{"$elemMatch":{"author" : "lison5", "content" : { "$regex" : ".*苍老师.*"}}}})
//备注：数组中对象数据要符合查询对象里面所有的字段，$全元素匹配，和顺序无关；

	@Test
	public void testObjArray3() throws ParseException {
		Bson eq = eq("author","lison5");
		Bson regex = regex("content", ".*苍老师.*");
		Bson elemMatch = Filters.elemMatch("comments", and(eq,regex));
		FindIterable<Document> find = collection.find(elemMatch);
		printOperation(find);	
	}
	
	
	// dbRef测试
	// dbref其实就是关联关系的信息载体，本身并不会去关联数据
	@Test
	public void dbRefTest() {
		FindIterable<Document> find = collection.find(eq("username", "lison"));
		printOperation(find);
	}

	
	private Block<Document> getBlock(final List<Document> ret) {
		Block<Document> printBlock = new Block<Document>() {
			@Override
			public void apply(Document t) {
				System.out.println("---------------------");
				CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry());
				final DocumentCodec codec = new DocumentCodec(codecRegistry, new BsonTypeClassMap());
				System.out.println(t.toJson(codec));
				System.out.println("---------------------");
				ret.add(t);
			}
		};
		return printBlock;
	}

	//打印查询出来的数据和查询的数据量
	private void printOperation( FindIterable<Document> find) {
		final List<Document> ret = new ArrayList<Document>();
		Block<Document> printBlock = getBlock(ret);
		find.forEach(printBlock);
		System.out.println(ret.size());
		ret.removeAll(ret);
	}

	
	private void printOperation(List<Document> ret, Block<Document> printBlock,
			AggregateIterable<Document> aggregate) {
		aggregate.forEach(printBlock);
		System.out.println(ret.size());
		ret.removeAll(ret);

	}
	



}