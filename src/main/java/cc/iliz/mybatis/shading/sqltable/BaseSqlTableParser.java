package cc.iliz.mybatis.shading.sqltable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.ParameterMapping;

import cc.iliz.mybatis.shading.strategy.StrategyRegister;
import cc.iliz.mybatis.shading.strategy.TableStrategy;

public abstract class BaseSqlTableParser implements SqlTableParser {
	private static final Log log = LogFactory.getLog(BaseSqlTableParser.class);

	@Override
	public String markShardingTable(String sql,Object param,List<ParameterMapping> parameterMappings) {
		Pattern pattern  = getRegPattern();
		Matcher matcher = pattern.matcher(sql);
		while(matcher.find()){
			String tableName = matcher.group(1);
			if(tableName != null && tableName.trim() != ""){
				String newTableName = getRealTableName(tableName);
				if(log.isDebugEnabled()){
					log.debug("get real table name of  table sharding sql [" + sql + "] is [" + newTableName +"]");
				}
				TableStrategy strategy = StrategyRegister.getInstance().getTableStrategy(newTableName);
				if(strategy != null){
					newTableName = strategy.getShadeTableName(this,tableName, param, parameterMappings);
					sql = sql.replaceAll(tableName, newTableName);
					//重置正则匹配sql
					matcher.reset(sql);
				}
			}
		}
		return sql;
	}
	
	public abstract Pattern getRegPattern();
	
	/**
	 * 根据正则取出的字符取真正的表名，如果以后有变化子类重新实现这个方法
	 * @param finder
	 * @return
	 */
	protected String getRealTableName(String tableName){
		return tableName.toLowerCase();
	}

}
