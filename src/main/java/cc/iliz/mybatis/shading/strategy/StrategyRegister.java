package cc.iliz.mybatis.shading.strategy;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.StringUtils;

import cc.iliz.mybatis.shading.annotation.Strategy;

public class StrategyRegister {
	private static final Log log = LogFactory.getLog(StrategyRegister.class);
	private Map<String,TableStrategy> register = new ConcurrentHashMap<>();
	private static StrategyRegister instance = new StrategyRegister();;
	
	private StrategyRegister(){}
	
	public static StrategyRegister getInstance(){
		return instance;
	}
	
	public void register(Strategy strategy,TableStrategy tableStrategy){
		synchronized(register){
			if(strategy != null){
				String[] tableNames = strategy.tableName().split(",");
				Arrays.stream(tableNames).forEach(name -> {
					register.put(name.toLowerCase(), tableStrategy);
					if (log.isDebugEnabled()) {
						log.debug("Table name [" + name.toLowerCase() + "] registe sharding strategy [" + tableStrategy.getClass().getName() + "] success");
					}
				});
				
			}
		}
	}

	public void register(String tableNames,TableStrategy tableStrategy){
		synchronized(register){
			if(StringUtils.hasText(tableNames)){
				String[] tableName = tableNames.split(",");
				Arrays.stream(tableName).forEach(name -> {
					register.put(name.toLowerCase(), tableStrategy);
					if (log.isDebugEnabled()) {
						log.debug("Table name [" + name.toLowerCase() + "] registe sharding strategy [" + tableStrategy.getClass().getName() + "] success");
					}
				});
			}
			
		}
	}
	
	public void register(String tableNames,Class<?> clazz){
		try {
			Object obj = clazz.newInstance();
			if(obj instanceof TableStrategy){
				TableStrategy tableStrategy = (TableStrategy)obj;
				register(tableNames,tableStrategy);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void register(Strategy strategy,Class<?> clazz){
		try {
			Object obj = clazz.newInstance();
			if(obj instanceof TableStrategy){
				TableStrategy tableStrategy = (TableStrategy)obj;
				register(strategy,tableStrategy);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void register(Class<?> clazz){
		Strategy strategy = clazz.getDeclaredAnnotation(Strategy.class);
		if(strategy != null){
			register(strategy,clazz);
		}
	}
	
	public void register(TableStrategy tableStrategy){
		Strategy strategy = tableStrategy.getClass().getDeclaredAnnotation(Strategy.class);
		if(strategy != null){
			register(strategy,tableStrategy);
		}
	}
	
	public void register(String tableNames,String className){
		try {
			Class<?> clazz = Class.forName(className);
			register(tableNames,clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public TableStrategy getTableStrategy(String tableName){
		return register.get(tableName);
	}
}
