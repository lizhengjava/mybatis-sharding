package cc.iliz.mybatis.shading.convert;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cc.iliz.mybatis.shading.strategy.StrategyRegister;
import cc.iliz.mybatis.shading.strategy.TestTable1TableStrategy;
import cc.iliz.mybatis.shading.strategy.User;

public class SqlConvertTest {
	ConverterFactory converterFactory = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		if(converterFactory == null){
			converterFactory = ConverterFactoryBuilder.getConverterFactoryBuilder().getConverterFacotry();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void convertSqlTest(){
		StrategyRegister.getInstance().register(TestTable1TableStrategy.class);
		User user = new User();
		user.setId(10);
		
		String sql = "select a.col_1,a.col_2,a.col_3 from app_test a where a.id in (select aid from app_test where col_1=1 and col_2=?) order by id desc";
		SqlConverter sqlConverter = converterFactory.getSqlConverter();
		sql = sqlConverter.convert(sql, null, user);
//		System.out.println(sql);
	}
	

}
