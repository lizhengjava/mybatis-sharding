package cc.iliz.mybatis.shading.sqltable;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.StringUtils;

import cc.iliz.mybatis.shading.db.ShardingContextHolder;
import cc.iliz.mybatis.shading.db.ShardingEntry;
import cc.iliz.mybatis.shading.db.ShardingProxyDataSource;
import cc.iliz.mybatis.shading.exception.ShardingException;
import cc.iliz.mybatis.shading.strategy.StrategyRegister;
import cc.iliz.mybatis.shading.strategy.TableStrategy;

public abstract class BaseSqlTableParser implements SqlTableParser {
	private static final Log log = LogFactory.getLog(BaseSqlTableParser.class);

	@Override
	public ShardingEntry markShardingTable(String sql, Object param) {
		Pattern pattern  = getRegPattern();
		Matcher matcher = pattern.matcher(sql);
		ShardingEntry entry = null;
		try {
			entry = sqlConvert(matcher,param);
		} catch (ShardingException e) {
			log.error("正在执行的sql: [" + sql + "] 无法分库分表，原因是：[" + e.getMessage() + "]");
		}
		return entry;
	}
	
	/**
	 * 取正则匹配模式
	 * @return pattern
	 */
	public abstract Pattern getRegPattern();
	
	/**
	 * 将匹配的sql转成分库后的sql
	 * @param matcher 正则匹配
	 * @param param 本次执行参数值
	 * @return 转换后的sql
	 * @throws ShardingException 非同一库以及未找到数据源异常
	 */
	protected ShardingEntry sqlConvert(Matcher matcher,Object param) throws ShardingException{
		ShardingEntry entry = new ShardingEntry();
		StringBuffer sb = new StringBuffer();
		ShardingProxyDataSource ds = null;
		while(matcher.find()){
			String g0 = matcher.group();
			String tableName = matcher.group(1);
			if(StringUtils.hasText(tableName)){
				String newTableName = tableNameConvert(getRealTableName(tableName).trim(),param);
				//是否同一库检查
				if(ds == null){
					ds = isSameDatabase(newTableName,null);
				}else{
					ShardingProxyDataSource tds = isSameDatabase(newTableName,ds);
					if(ds != tds){
						throw new ShardingException("非同一数据库，无法执行分库操作");
					}
				}
				g0 = g0.replaceAll(tableName, newTableName);
			}
			matcher.appendReplacement(sb, g0);
		}
		matcher.appendTail(sb);
		if(ds != null){
			entry.setProxy(ds);
		}else{
			throw new ShardingException("未找到此sql中的数据表对应的数据源，请确定数据源配置。");
		}
		entry.setSql(sb.toString());
		return entry;
	}
	
	private ShardingProxyDataSource isSameDatabase(String newTableName,ShardingProxyDataSource datasource){
		//优先查看当前数据源表
		if(datasource != null){
			if(datasource.checkDataSourceByTableName(newTableName)){
				return datasource;
			}
		}
		
		Set<ShardingProxyDataSource> set = ShardingContextHolder.getShardingProxyDataSource();
		for(ShardingProxyDataSource s : set){
			if(s.checkDataSourceByTableName(newTableName)){
				return s;
			}
		}
		
		return null;
	}
	
	/**
	 * 根据自定义策略转换表名
	 * @param tableName 原表名
	 * @param param 本次执行参数值
	 * @return 转换后的表名
	 */
	protected String tableNameConvert(String tableName,Object param){
		TableStrategy strategy = StrategyRegister.getInstance().getTableStrategy(tableName);
		if(strategy != null){
			return strategy.getShadeTableName(this,tableName, param);
		}
		return tableName;
	}
	
	/**
	 * 根据正则取出的字符取真正的表名，如果以后有变化子类重新实现这个方法
	 * @param tableName original table name
	 * @return the real table name
	 */
	protected String getRealTableName(String tableName){
		return tableName.toLowerCase();
	}

}
