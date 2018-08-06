package cc.iliz.mybatis.shading.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cc.iliz.mybatis.shading.plugin.domain.AppTestDO;
import cc.iliz.mybatis.shading.plugin.domain.AppTestMapper;

public class TableShardPluginTest {
	static SqlSessionFactory sqlSessionFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DataSource dataSource = BaseDataTest.createTestDataSource();
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("Production", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.setLazyLoadingEnabled(true);
		configuration.getTypeAliasRegistry().registerAlias(AppTestDO.class);
		configuration.addMapper(AppTestMapper.class);
		configuration.addInterceptor(new TableShardPlugin());
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void pluginTest() {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			AppTestMapper mapper = session.getMapper(AppTestMapper.class);
			
			AppTestDO test = new AppTestDO();
			test.setId(1000);
			test.setCnt("test");
			test.setDeleted('1');
			
			int result = mapper.insert2(test);
			AppTestDO list = mapper.getOne(test);
			System.out.println(list);
			assertEquals(1,result);
		} finally {
			session.close();
		}

	}

}
