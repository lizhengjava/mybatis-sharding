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
			return list.size() > 0 ? list.get(0).getDatasourceName() : null;
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
				//无自定义数据源处理
				Map<String,Set<String>> map = shardingEntry.getDbTables();
				
				Iterator<Entry<String,Set<String>>> it = map.entrySet().iterator();
				//如果只有一条记录直接取结果,否则取最多条表记录
				if(map.size() == 1){
					return getDataSourceByName(it.next().getKey()).getConnection();
				}else if(map.size() > 1){
					//如果大于一条记录，取sql表最多的一个
					int max = 0;
					Entry<String,Set<String>> et = null;
					while(it.hasNext()){
						Entry<String,Set<String>> te = it.next();
						if(te.getValue().size() > max){
							max = te.getValue().size();
							et = te;
						}
					}
					return getDataSourceByName(et.getKey()).getConnection();
				}
			}
		}catch(Exception e){
			log.error("获取数据库连接异常，异常信息为：[" + e.getMessage() + "]");
		}
		return null;
	}

}
