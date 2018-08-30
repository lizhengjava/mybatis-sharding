package cc.iliz.mybatis.shading.plugin;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import cc.iliz.mybatis.shading.db.DbShardingConnectionProxy;
import cc.iliz.mybatis.shading.db.ShardingContextHolder;
import cc.iliz.mybatis.shading.db.ShardingEntry;
import cc.iliz.mybatis.shading.db.ShardingProxyDataSource;
import cc.iliz.mybatis.shading.parse.XmlConfigParser;
import cc.iliz.mybatis.shading.sqltable.SqlTableParser;
import cc.iliz.mybatis.shading.sqltable.SqlTableParserFactory;
import cc.iliz.mybatis.shading.strategy.StrategyRegister;
import cc.iliz.mybatis.shading.strategy.TableStrategy;
import cc.iliz.mybatis.shading.util.ReflectionUtils;

@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class TableShardPlugin implements Interceptor {
	private static final Log log = LogFactory.getLog(TableShardPlugin.class);

	public static final String SHARDING_CONFIG = "shardingConfig";
	public static final String STRATEGY_CONFIG = "packageNames";
	private String scanPackage;
	private static AtomicBoolean parsed = new AtomicBoolean(false);

	private ApplicationContext applicationContext;

	public String getScanPackage() {
		return scanPackage;
	}

	public void setScanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		//table sharding
		if (invocation.getTarget() instanceof StatementHandler) {
			if (!parsed.get()) {
				parseAnnotationAuto();
			}
			StatementHandler handler = (StatementHandler) invocation.getTarget();
			String sql = handler.getBoundSql().getSql();
			if (log.isDebugEnabled()) {
				log.debug("table sharding orginal sql is [" + sql + "]");
			}
			SqlTableParser sqlTableParser = null;
			try{
				sqlTableParser = applicationContext.getBean(SqlTableParser.class);
			}catch(Exception e){
				log.debug("业务系统无SqlTableParser实现");
			}
			ShardingEntry entry = null;
			if(sqlTableParser != null){
				entry = sqlTableParser.markShardingTable(sql, handler.getBoundSql().getParameterObject());
			}else{
				entry = SqlTableParserFactory.getInstance().getSqlTableParser().markShardingTable(sql, handler.getBoundSql().getParameterObject());
			}

			if (log.isDebugEnabled()) {
				log.debug("table sharding converted sql is [" + sql + "]");
			}
			ReflectionUtils.setFieldValue(handler.getBoundSql(), "sql", entry.getSql());
			
			//db sharding
			ShardingProxyDataSource datasource = entry.getProxy();
			if(invocation.getArgs()[0] instanceof Connection && datasource != null){
				Connection con = datasource.getConnection();
				if(invocation.getArgs()[0] instanceof Proxy && invocation.getArgs()[0].toString().contains("ConnectionLogger")){
					
					ConnectionLogger logger = (ConnectionLogger)ReflectionUtils.getFieldValue(invocation.getArgs()[0], "h");
					con = ConnectionLogger.newInstance(con, (Log)ReflectionUtils.getFieldValue(logger, "statementLog"), (int)ReflectionUtils.getFieldValue(logger, "queryStack"));
				}
				//proxy connection
				invocation.getArgs()[0] = getShardingConnection(con);
			}
		}
		return invocation.proceed();
	}
	
	private Connection getShardingConnection(Connection con){
		return (Connection) Proxy.newProxyInstance(
				con.getClass().getClassLoader(),
				new Class[] {Connection.class},
				new DbShardingConnectionProxy(con));
	}
	

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// 只解析一次
		if (parsed.get()) {
			return;
		}
		// 解析配置文件
		String config = properties.getProperty(SHARDING_CONFIG, null);
		if (log.isDebugEnabled()) {
			log.debug("sharding config is ：[" + config + "]");
		}
		if (config != null && config.trim().length() > 0) {
			XmlConfigParser parser = new XmlConfigParser(config);
			parser.parse();
		}

		// 扫描Strategt annotation配置
		String sconfig = properties.getProperty(STRATEGY_CONFIG, null);
		if (log.isDebugEnabled()) {
			log.debug("strategy scan config is ：[" + sconfig + "]");
		}
		ResolverUtil<TableStrategy> resolverUtil = new ResolverUtil<TableStrategy>();
		Set<Class<? extends TableStrategy>> tableStrategys = null;
		if (sconfig != null && sconfig.trim().length() > 0) {
			tableStrategys = resolverUtil.findImplementations(TableStrategy.class, sconfig.split(",")).getClasses();

			StrategyRegister register = StrategyRegister.getInstance();
			tableStrategys.stream().forEach(t -> register.register(t));
		}

		if (log.isDebugEnabled()) {
			log.debug("table strategy config parse success.");
		}
		parsed.set(true);
	}

	private void parseAnnotationAuto() {
		if (this.applicationContext != null) {
			Map<String, TableStrategy> strategys = applicationContext.getBeansOfType(TableStrategy.class);
			StrategyRegister register = StrategyRegister.getInstance();
			strategys.entrySet().stream().forEach(t -> register.register(t.getValue()));
		} 
		if(StringUtils.hasText(this.scanPackage)){
			ResolverUtil<TableStrategy> resolverUtil = new ResolverUtil<TableStrategy>();
			Set<Class<? extends TableStrategy>> tableStrategys = resolverUtil
					.findImplementations(TableStrategy.class, scanPackage.split(",")).getClasses();
			if (log.isDebugEnabled()) {
				log.debug("auto scaned annotation ：[" + scanPackage + "], table sharding strategy list is ："
						+ tableStrategys.toString());
			}
			StrategyRegister register = StrategyRegister.getInstance();
			tableStrategys.stream().forEach(t -> register.register(t));
			parsed.set(true);
			if (log.isDebugEnabled()) {
				log.debug("success scaned annotation automatically。scanned package is ：[" + scanPackage + "]");
			}
		}
		parsed.set(true);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;

		//分库数据源准备
		Map<String, DataSource> datasources = applicationContext.getBeansOfType(DataSource.class);
		datasources.entrySet().forEach(entry->{
			ShardingProxyDataSource instance = ShardingProxyDataSource.instanceBuilder(entry.getValue(), 1);
			ShardingContextHolder.addShardingProxyDataSource(instance);
		});
	}

}
