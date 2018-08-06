package cc.iliz.mybatis.shading.plugin;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cc.iliz.mybatis.shading.plugin.domain.AppTestDO;
import cc.iliz.mybatis.shading.plugin.domain.AppTestMapper;

public class TableXmlShardPluginTest {
	static SqlSessionFactory sqlSessionFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 String resource = "MapperConfig.xml";
		 Reader reader = Resources.getResourceAsReader(resource);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
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
	public void pluginTest() {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			AppTestMapper mapper = session.getMapper(AppTestMapper.class);
			
			AppTestDO test = new AppTestDO();
			test.setId(1000);
			test.setCnt("test");
			test.setDeleted('1');
			
			int result = mapper.insert2(test);
			List<AppTestDO> list = mapper.getList(test);
			System.out.println(list);
			assertEquals(1,result);
		} finally {
			session.close();
		}

	}
}
