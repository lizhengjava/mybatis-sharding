package cc.iliz.mybatis.shading.db;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

public class ShardingContextHolder {
	private static Set<ShardingProxyDataSource> dataSourceHolderSet = new HashSet<>();
	
	public static void setShardingProxyDataSources(Set<ShardingProxyDataSource> values){
		dataSourceHolderSet = values;
	}
	
	public static void addShardingProxyDataSource(ShardingProxyDataSource value){
		dataSourceHolderSet.add(value);
	}
	
	public static Set<ShardingProxyDataSource> getShardingProxyDataSource(){
		return dataSourceHolderSet;
	}
	
	public static ShardingProxyDataSource getShardingProxyDataSourceByTableName(String tableName){
		if(dataSourceHolderSet != null){
			List<ShardingProxyDataSource> list = dataSourceHolderSet.stream().filter(value -> {
				return value.checkDataSourceByTableName(tableName);
			}).collect(Collectors.toList());
			
			Collections.sort(list);
			return list.get(0);
		}
		return null;
	}
	
	public static ShardingProxyDataSource getShardingProxyDataSourceByDataSource(DataSource dataSource){
		if(dataSourceHolderSet != null){
			List<ShardingProxyDataSource> list = dataSourceHolderSet.stream().filter(value -> {
				return value.getDataSource().equals(dataSource);
			}).collect(Collectors.toList());
			
			Collections.sort(list);
			return list.get(0);
		}
		return null;
	}
}
