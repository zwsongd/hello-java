package cn.enjoy.mg;

import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Filters.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.mongodb.WriteConcern;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;


import cn.enjoy.entity.Address;
import cn.enjoy.entity.Favorites;
import cn.enjoy.entity.User;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

//原生java驱动 Pojo的操作方式
public class QuickStartJavaPojoTest {



    private MongoDatabase db;
    
    private MongoCollection<User> doc;
    
    private MongoClient client;
	
	
    @Before
    public void init(){
    	//编解码器的list
    	List<CodecRegistry> codecResgistes = new ArrayList<>();
    	//list加入默认的编解码器集合
    	codecResgistes.add(MongoClient.getDefaultCodecRegistry());
    	//生成一个pojo的编解码器
    	CodecRegistry pojoCodecRegistry = CodecRegistries.
    			fromProviders(PojoCodecProvider.builder().automatic(true).build());
    	//list加入pojo的编解码器
    	codecResgistes.add(pojoCodecRegistry);
    	//通过编解码器的list生成编解码器注册中心
    	CodecRegistry registry = CodecRegistries.fromRegistries(codecResgistes);
    	
    	//把编解码器注册中心放入MongoClientOptions
    	//MongoClientOptions相当于连接池的配置信息
    	MongoClientOptions build = MongoClientOptions.builder().
    			codecRegistry(registry).build();


    	ServerAddress serverAddress = new ServerAddress("192.168.244.123", 27017);

		client = new MongoClient(serverAddress, build);
		db =client.getDatabase("lison");
		doc = db.getCollection("users",User.class);
    }
    

    
    @Test
    public void insertDemo(){
    	User user = new User();
    	user.setUsername("cang");
    	user.setCountry("USA");
    	user.setAge(20);
    	user.setLenght(1.77f);
    	user.setSalary(new BigDecimal("6265.22"));
    	
    	//添加“address”子文档
    	Address address1 = new Address();
    	address1.setaCode("411222");
    	address1.setAdd("sdfsdf");
    	user.setAddress(address1);
    	
    	//添加“favorites”子文档，其中两个属性是数组
    	Favorites favorites1 = new Favorites();
    	favorites1.setCites(Arrays.asList("东莞","东京"));
    	favorites1.setMovies(Arrays.asList("西游记","一路向西"));
    	user.setFavorites(favorites1);
    	
    	
    	User user1 = new User();
    	user1.setUsername("chen");
    	user1.setCountry("China");
    	user1.setAge(30);
    	user1.setLenght(1.77f);
    	user1.setSalary(new BigDecimal("6885.22"));
    	Address address2 = new Address();
    	address2.setaCode("411000");
    	address2.setAdd("我的地址2");
    	user1.setAddress(address2);
    	Favorites favorites2 = new Favorites();
    	favorites2.setCites(Arrays.asList("珠海","东京"));
    	favorites2.setMovies(Arrays.asList("东游记","一路向东"));
    	user1.setFavorites(favorites2);
    	
    	
    	//使用insertMany插入多条数据
    	doc.insertMany(Arrays.asList(user,user1));
    	
    }
    
    
    @Test
    public void testFind(){
    	
    	final List<User> ret = new ArrayList<>();
		Consumer<User> printDocument = new Consumer<User>() {
			@Override
			public void accept(User t) {
				System.out.println(t.toString());
				ret.add(t);
			}
    		
		};
		
    	//select * from users  where favorites.cites has "东莞"、"东京"
		//db.users.find({ "favorites.cites" : { "$all" : [ "东莞" , "东京"]}})
		Bson all = all("favorites.cites", Arrays.asList("东莞","东京"));//定义数据过滤器，喜欢的城市中要包含"东莞"、"东京"
		FindIterable<User> find = doc.find(all);
		find.forEach(printDocument);
		System.out.println("------------------>"+String.valueOf(ret.size()));
		ret.removeAll(ret);
    	
    	//select * from users  where username like '%s%' and (contry= English or contry = USA)
		// db.users.find({ "$and" : [ { "username" : { "$regex" : ".*c.*"}} , { "$or" : [ { "country" : "English"} , { "country" : "USA"}]}]})
		String regexStr = ".*c.*";
		Bson regex = regex("username", regexStr);//定义数据过滤器，username like '%s%'
		Bson or = or(eq("country","English"),eq("country","USA"));//定义数据过滤器，(contry= English or contry = USA)
		FindIterable<User> find2 = doc.find(and(regex,or));
		find2.forEach(printDocument);
		System.out.println("------------------>"+String.valueOf(ret.size()));

    }
    
    
    @Test
    public void testUpdate(){
       	//update  users  set age=6 where username = 'lison' 
       //db.users.updateMany({ "username" : "lison"},{ "$set" : { "age" : 6}},true)
    	Bson eq = eq("username", "lison");//定义数据过滤器，username = 'lison' 
		Bson set = set("age", 8);//更新的字段.来自于Updates包的静态导入
		UpdateResult updateMany = doc.updateMany(eq, set);
		System.out.println("------------------>"+String.valueOf(updateMany.getModifiedCount()));//打印受影响的行数
    	
    	//update users  set favorites.movies add "小电影2 ", "小电影3" where favorites.cites  has "东莞"
    	//db.users.updateMany({ "favorites.cites" : "东莞"}, { "$addToSet" : { "favorites.movies" : { "$each" : [ "小电影2 " , "小电影3"]}}},true)
    	Bson eq2 = eq("favorites.cites", "东莞");//定义数据过滤器，favorites.cites  has "东莞"
		Bson addEachToSet = addEachToSet("favorites.movies", Arrays.asList( "小电影2 ", "小电影3"));//更新的字段.来自于Updates包的静态导入
		UpdateResult updateMany2 = doc.updateMany(eq2, addEachToSet);
		System.out.println("------------------>"+String.valueOf(updateMany2.getModifiedCount()));
    }
    
    @Test
    public void testDelete(){
    	
    	//delete from users where username = ‘lison’
    	//db.users.deleteMany({ "username" : "lison"} )
    	Bson eq = eq("username", "lison");//定义数据过滤器，username='lison'
		DeleteResult deleteMany = doc.deleteMany(eq);
		System.out.println("------------------>"+String.valueOf(deleteMany.getDeletedCount()));//打印受影响的行数
    	
    	//delete from users where age >8 and age <25
        //db.users.deleteMany({"$and" : [ {"age" : {"$gt": 8}} , {"age" : {"$lt" : 25}}]})
    	Bson gt = gt("age",8);//定义数据过滤器，age > 8，所有过滤器的定义来自于Filter这个包的静态方法，需要频繁使用所以静态导入
    	
		Bson lt = lt("age",25);//定义数据过滤器，age < 25
		Bson and = and(gt,lt);//定义数据过滤器，将条件用and拼接
		DeleteResult deleteMany2 = doc.deleteMany(and);
		System.out.println("------------------>"+String.valueOf(deleteMany2.getDeletedCount()));//打印受影响的行数
    }
    

    

    
	
	
	

}