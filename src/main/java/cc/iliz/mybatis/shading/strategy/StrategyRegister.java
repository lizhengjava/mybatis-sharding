package cc.iliz.mybatis.shading.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import cc.iliz.mybatis.shading.annotation.Strategy;

public class StrategyRegister {
	private static final Log log = LogFactory.getLog(StrategyRegister.class);
	private Map<String,TableStrategy> register = new ConcurrentHashMap<>();
	private static StrategyRegister instance = new StrategyRegister();;
	
	private StrategyRegister(){}
	
	public static StrategyRegister getInstance(){
		return instance;
	}

	public void register(String tableName,TableStrategy tableStrategy){
		Strategy strategy = tableStrategy.getClass().getDeclaredAnnotation(Strategy.class);
		synchronized(register){
			if(strategy != null){
				register.put(strategy.tableName().toLowerCase(), tableStrategy);

				if (log.isDebugEnabled()) {
					log.debug("Table name [" + strategy.tableName().toLowerCase() + "] registe sharding strategy [" + tableStrategy.getClass().getName() + "] success");
				}
			}else{
				register.put(tableName.toLowerCase(), tableStrategy);
				if (log.isDebugEnabled()) {
					log.debug("Table name [" + tableName.toLowerCase() + "] registe sharding strategy [" + tableStrategy.getClass().getName() + "] success");
				}
			}
			
		}
	}
	
	public void register(String tableName,Class<?> clazz){
		try {
			Object obj = clazz.newInstance();
			if(obj instanceof TableStrategy){
				TableStrategy tableStrategy = (TableStrategy)obj;
				register(tableName,tableStrategy);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void register(Class<?> clazz){
		Strategy strategy = clazz.getDeclaredAnnotation(Strategy.class);
		if(strategy != null){
			register(strategy.tableName(),clazz);
		}
	}
	
	public void register(String tableName,String className){
		try {
			Class<?> clazz = Class.forName(className);
			register(tableName,clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public TableStrategy getTableStrategy(String tableName){
		return register.get(tableName);
	}
}
