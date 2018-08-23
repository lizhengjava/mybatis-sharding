package cc.iliz.mybatis.shading.sqltable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.ParameterMapping;
import org.springframework.util.StringUtils;

import cc.iliz.mybatis.shading.strategy.StrategyRegister;
import cc.iliz.mybatis.shading.strategy.TableStrategy;

public abstract class BaseSqlTableParser implements SqlTableParser {
	private static final Log log = LogFactory.getLog(BaseSqlTableParser.class);

	@Override
	public String markShardingTable(String sql,Object param,List<ParameterMapping> parameterMappings) {
		Pattern pattern  = getRegPattern();
		Matcher matcher = pattern.matcher(sql);
		sql = sqlConvert(matcher,param,parameterMappings);
		return sql;
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
	 * @param parameterMappings 本次执行参数映射
	 * @return
	 */
	protected String sqlConvert(Matcher matcher,Object param,List<ParameterMapping> parameterMappings){
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			String g0 = matcher.group();
			String tableName = matcher.group(1);
			if(StringUtils.hasText(tableName)){
				String newTableName = tableNameConvert(getRealTableName(tableName).trim(),param,parameterMappings);
				if(log.isDebugEnabled()){
					log.debug("get real table name is [" + newTableName +"]");
				}
				g0 = g0.replaceAll(tableName, newTableName);
			}
			matcher.appendReplacement(sb, g0);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	/**
	 * 根据自定义策略转换表名
	 * @param tableName 原表名
	 * @param param 本次执行参数值
	 * @param parameterMappings 本次执行参数映射
	 * @return
	 */
	protected String tableNameConvert(String tableName,Object param,List<ParameterMapping> parameterMappings){
		TableStrategy strategy = StrategyRegister.getInstance().getTableStrategy(tableName);
		if(strategy != null){
			return strategy.getShadeTableName(this,tableName, param, parameterMappings);
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
