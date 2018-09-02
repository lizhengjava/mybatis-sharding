package cc.iliz.mybatis.shading.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.StringUtils;

import cc.iliz.mybatis.shading.strategy.DbStrategy;

public class DbShardingConnectionProxy implements InvocationHandler {
	private static final Log log = LogFactory.getLog(DbShardingConnectionProxy.class);
	private static Set<ShardingProxyDataSource> dataSourceHolderSet = new TreeSet<ShardingProxyDataSource>(new ShardingProxyDataSource());
	private ShardingEntry shardingEntry;
	private DbStrategy dbStrategy;
	
	public static void addShardingProxyDataSource(ShardingProxyDataSource value){
		dataSourceHolderSet.add(value);
	}
	
	public static String getDataSourceNameByTableName(String tableName,String datasourceName){
		if(dataSourceHolderSet != null){
			Stream<ShardingProxyDataSource> stream = dataSourceHolderSet.stream();
			if(StringUtils.hasText(datasourceName)){
				stream = stream.filter(value -> {
					return value.getDatasourceName().equalsIgnoreCase(datasourceName);
				});
			}
			List<ShardingProxyDataSource> list = stream.filter(value -> {
				return value.checkDataSourceByTableName(tableName);
			}).collect(Collectors.toList());
			
			Collections.sort(list);
			return list.get(0).getDatasourceName();
		}
		return null;
	}
	
	public static String getDataSourceNameByDataSource(DataSource dataSource){
		if(dataSourceHolderSet != null){
			List<ShardingProxyDataSource> list = dataSourceHolderSet.stream().filter(value -> {
				return value.getDataSource().equals(dataSource);
			}).collect(Collectors.toList());
			
			Collections.sort(list);
			return list.get(0).getDatasourceName();
		}
		return null;
	}
	
	private DataSource getDataSourceByName(String dbName){
		if(dataSourceHolderSet != null){
			List<ShardingProxyDataSource> list = dataSourceHolderSet.stream().filter(value -> {
				return value.getDatasourceName().equalsIgnoreCase(dbName);
			}).collect(Collectors.toList());
			
			Collections.sort(list);
			return list.get(0).getDataSource();
		}
		return null;
	}
	
	public DbShardingConnectionProxy(){}
	
	public DbShardingConnectionProxy(ShardingEntry shardingEntry,DbStrategy dbStrategy){
		this.shardingEntry = shardingEntry;
		this.dbStrategy = dbStrategy;
	}

	public void setTarget(ShardingEntry shardingEntry) {
		this.shardingEntry = shardingEntry;
	}

	public void setDbStrategy(DbStrategy dbStrategy) {
		this.dbStrategy = dbStrategy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Connection con = getTarget();
		return method.invoke(con, args);
	}
	
	/**
	 * 实现分库路由
	 * @return 数据源
	 */
	private Connection getTarget(){
		try{
			if(dbStrategy != null){
				String dbName = dbStrategy.getShardingDataSource(shardingEntry);
				return getDataSourceByName(dbName).getConnection();
			}else{
				Map<String,Set<String>> map = shardingEntry.getDbTables();
				Iterator<Entry<String,Set<String>>> it = map.entrySet().iterator();
				if(it.hasNext()){
					return getDataSourceByName(it.next().getKey()).getConnection();
				}
			}
		}catch(Exception e){
			log.error("获取数据库连接异常，异常信息为：[" + e.getMessage() + "]");
		}
		return null;
	}

}
