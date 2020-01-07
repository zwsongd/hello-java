package cn.enjoy.mg;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import cn.enjoy.entity.Address;
import cn.enjoy.entity.Favorites;
import cn.enjoy.entity.User;
import cn.enjoy.service.UserService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//spring Pojo的操作方式
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class QuickStartSpringPojoTest {


    @Resource
	private MongoOperations tempelate;

    @Resource
	private UserService userService;
	

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
    	
    	tempelate.insertAll(Arrays.asList(user,user1));
    }
    
    @Test
    public void testFind(){
    	
    	//select * from users  where favorites.cites has "东莞"、"东京"
		//db.users.find({ "favorites.cites" : { "$all" : [ "东莞" , "东京"]}})
    	Criteria all = where("favorites.cites").all(Arrays.asList("东莞","东京"));
		List<User> find = tempelate.find(query(all), User.class);
    	System.out.println(find.size());
    	for (User user : find) {
			System.out.println(user.toString());
		}
    	
    	
    	//select * from users  where username like '%s%' and (contry= English or contry = USA)
		// db.users.find({ "$and" : [ { "username" : { "$regex" : ".*s.*"}} , { "$or" : [ { "country" : "English"} , { "country" : "USA"}]}]})
    	String regexStr = ".*c.*";
    	//username like '%s%'
    	Criteria regex = where("username").regex(regexStr);
    	//contry= EngLish
    	Criteria or1 = where("country").is("English");
    	//contry= USA
    	Criteria or2 = where("country").is("USA");
    	
    	Criteria or = new Criteria().orOperator(or1,or2);

    	
    	Query query = query(new Criteria().andOperator(regex,or));
    	
    	List<User> find2 = tempelate.find(query, User.class);
    	
    	System.out.println(find2.size());
    	for (User user : find2) {
			System.out.println(user.toString());
		}
    }
    
    
    @Test
    public void testUpdate(){
       	//update  users  set age=6 where username = 'lison' 
       //db.users.updateMany({ "username" : "lison"},{ "$set" : { "age" : 6}},true)
    	Query query = query(where("username").is("lison"));
		Update update = update("age", 6);
		UpdateResult updateFirst = tempelate.updateMulti(query, update, User.class);
    	System.out.println(updateFirst.getModifiedCount());
    	
    	//update users  set favorites.movies add "小电影2 ", "小电影3" where favorites.cites  has "东莞"
    	//db.users.updateMany({ "favorites.cites" : "东莞"}, { "$addToSet" : { "favorites.movies" : { "$each" : [ "小电影2 " , "小电影3"]}}},true)
    	query = query(where("favorites.cites").is("东莞"));
		update = new Update().addToSet("favorites.movies").each("小电影2 ", "小电影3");
		UpdateResult updateMulti = tempelate.updateMulti(query, update, User.class);
		System.out.println("--------------------->"+updateMulti.getModifiedCount());
    }
    
    @Test
    public void testDelete(){
    	
    	//delete from users where username = ‘lison’
    	//db.users.deleteMany({ "username" : "lison"} )
    	Query query = query(where("username").is("lison"));
		DeleteResult remove = tempelate.remove(query, User.class);
    	System.out.println("--------------------->"+remove.getDeletedCount());
    	
    	//delete from users where age >8 and age <25
        //db.users.deleteMany({"$and" : [ {"age" : {"$gt": 8}} , {"age" : {"$lt" : 25}}]})
    	query = query(new Criteria().andOperator(where("age").gt(8),where("age").lt(25)));
		DeleteResult remove2 = tempelate.remove(query, User.class);
    	System.out.println("--------------------->"+remove2.getDeletedCount());
    }

	@Test
	public void doTransaction() {
		userService.doTransaction();
	}
	
}