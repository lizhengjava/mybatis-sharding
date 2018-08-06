package cc.iliz.mybatis.shading.parse;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class XmlConfigParseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void parseTest() throws IOException{
		String confiPath = "sharding_config1.xml";
		XmlConfigParser parser = new XmlConfigParser(confiPath);
		parser.parse();
	}

}
