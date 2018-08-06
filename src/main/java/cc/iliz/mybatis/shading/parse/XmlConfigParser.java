package cc.iliz.mybatis.shading.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;

import cc.iliz.mybatis.shading.strategy.StrategyRegister;

public class XmlConfigParser {
	private static final Log log = LogFactory.getLog(XmlConfigParser.class);
	
	private Boolean parsed=false;
	private XPathParser parser;

	public XmlConfigParser(String path){
		try {
			InputStream input = Resources.getResourceAsStream(path);
			parser = new XPathParser(input,false,null,new ShardingConfigEntityResolver());
		} catch (IOException e) {
			log.error("table sharding xml config init error." ,e);
		}
	}

	public XmlConfigParser(Reader reader) {
		parser = new XPathParser(reader,false,null,new ShardingConfigEntityResolver());
	}

	public XmlConfigParser(InputStream inputstream) {
		parser = new XPathParser(inputstream,false,null,new ShardingConfigEntityResolver());
	}
	

	public void parse() {
		if (parsed) {
			throw new BuilderException("Each sharding config can only be used once.");
		}
		XNode root = parser.evalNode("shardingConfig");
		parseShardingConfig(root.evalNode("strategy"));
	}

	private void parseShardingConfig(XNode root) {
		if (root != null) {
			String tableName = root.getStringAttribute("tableName");
			String strategyClass = root.getStringAttribute("strategyClass");

			if(log.isDebugEnabled()){
				log.debug("table sharding xml config table name is [" + tableName + "]");
				log.debug("table sharding xml config strategy class is [" + strategyClass + "]");
			}

			StrategyRegister register = StrategyRegister.getInstance();
			register.register(tableName, strategyClass);
		}
	}
}
