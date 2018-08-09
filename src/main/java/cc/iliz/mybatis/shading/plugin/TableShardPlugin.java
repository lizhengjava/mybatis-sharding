package cc.iliz.mybatis.shading.plugin;

import java.sql.Connection;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import cc.iliz.mybatis.shading.convert.ConverterFactory;
import cc.iliz.mybatis.shading.convert.ConverterFactoryBuilder;
import cc.iliz.mybatis.shading.parse.XmlConfigParser;
import cc.iliz.mybatis.shading.strategy.StrategyRegister;
import cc.iliz.mybatis.shading.strategy.TableStrategy;
import cc.iliz.mybatis.shading.util.ReflectionUtils;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class TableShardPlugin implements Interceptor {
	private static final Log log = LogFactory.getLog(TableShardPlugin.class);
	
	private static final String SHARDING_CONFIG = "shardingConfig";
	private static final String STRATEGY_CONFIG = "packageNames";
	private static String scanPackage = "com,org,edu,cn,gov,io,cc";
	private static AtomicBoolean parsed = new AtomicBoolean(false);
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (invocation.getTarget() instanceof StatementHandler) {
			if(!parsed.get()){
				parseAnnotationAuto();
			}
			StatementHandler handler = (StatementHandler) invocation.getTarget();
			String sql = handler.getBoundSql().getSql();
			if(log.isDebugEnabled()){
				log.debug("table sharding orginal sql is [" + sql + "]");
			}
			ConverterFactory factory = ConverterFactoryBuilder.getConverterFactoryBuilder().getConverterFacotry();
			sql = factory.getSqlConverter().convert(sql, handler.getBoundSql().getParameterMappings(),
					handler.getBoundSql().getParameterObject());

			if(log.isDebugEnabled()){
				log.debug("table sharding converted sql is [" + sql + "]");
			}
			ReflectionUtils.setFieldValue(handler.getBoundSql(), "sql", sql);
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		//只解析一次
		if(parsed.get()){
			return;
		}
		// 解析配置文件
		String config = properties.getProperty(SHARDING_CONFIG, null);
		if(log.isDebugEnabled()){
			log.debug("sharding config is ：[" + config + "]");
		}
		if (config != null && config.trim().length() > 0) {
			XmlConfigParser parser = new XmlConfigParser(config);
			parser.parse();
		}
		
		//扫描Strategt annotation配置
		String sconfig = properties.getProperty(STRATEGY_CONFIG, null);
		if(log.isDebugEnabled()){
			log.debug("strategy scan config is ：[" + sconfig + "]");
		}
		ResolverUtil<TableStrategy> resolverUtil = new ResolverUtil<TableStrategy>();
		Set<Class<? extends TableStrategy>> tableStrategys = null;
		if (sconfig != null && sconfig.trim().length() > 0) {
			tableStrategys = resolverUtil.findImplementations(TableStrategy.class, sconfig.split(",")).getClasses();

			StrategyRegister register = StrategyRegister.getInstance();
			tableStrategys.stream().forEach(t->register.register(t));
		}

		if(log.isDebugEnabled()){
			log.debug("table strategy config parse success.");
		}
		parsed.set(true);
	}
	
	private void parseAnnotationAuto(){
		ResolverUtil<TableStrategy> resolverUtil = new ResolverUtil<TableStrategy>();
		Set<Class<? extends TableStrategy>> tableStrategys = resolverUtil.findImplementations(TableStrategy.class, scanPackage.split(",")).getClasses();
		if(log.isDebugEnabled()){
			log.debug("auto scaned annotation ：[" + scanPackage + "], table sharding strategy list is ：" +tableStrategys.toString());
		}
		StrategyRegister register = StrategyRegister.getInstance();
		tableStrategys.stream().forEach(t->register.register(t));
		parsed.set(true);
		if(log.isDebugEnabled()){
			log.debug("success scaned annotation automatically。scanned package is ：[" + scanPackage +"]");
		}
		
	}

}
