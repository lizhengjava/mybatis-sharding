package cc.iliz.mybatis.shading.sqltable;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.util.StringUtils;

import cc.iliz.mybatis.shading.db.DbShardingConnectionProxy;
import cc.iliz.mybatis.shading.db.ShardingEntry;
import cc.iliz.mybatis.shading.strategy.StrategyRegister;
import cc.iliz.mybatis.shading.strategy.TableStrategy;

public abstract class BaseSqlTableParser implements SqlTableParser {
//	private static final Log log = LogFactory.getLog(BaseSqlTableParser.class);
	private SqlCommandType sqlCommandType ;

	@Override
	public ShardingEntry markShardingTable(String sql, Object param) {
		Pattern pattern  = getRegPattern();
		Matcher matcher = pattern.matcher(sql);
		ShardingEntry entry = sqlConvert(matcher,param);
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
	 */
	protected ShardingEntry sqlConvert(Matcher matcher,Object param){
		ShardingEntry entry = new ShardingEntry();
		StringBuffer sb = new StringBuffer();
		Set<String> names = new HashSet<>();
		String dbName = null;
		while(matcher.find()){
			String g0 = matcher.group();
			String tableName = matcher.group(1);
			if(StringUtils.hasText(tableName)){
				String newTableName = tableNameConvert(getRealTableName(tableName).trim(),param);
				dbName = DbShardingConnectionProxy.getDataSourceNameByTableName(newTableName,dbName);
				entry.addItemToDbTables(dbName, newTableName);
				//是否同一库检查
				names.add(newTableName);
				g0 = g0.replaceAll(tableName, newTableName);
			}
			matcher.appendReplacement(sb, g0);
		}
		matcher.appendTail(sb);
		entry.setNames(names);
		entry.setSqlCommandType(this.getSqlCommandType());
		entry.setSql(sb.toString());
		return entry;
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

	@Override
	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}

	public void setSqlCommandType(SqlCommandType sqlCommandType) {
		this.sqlCommandType = sqlCommandType;
	}
}
