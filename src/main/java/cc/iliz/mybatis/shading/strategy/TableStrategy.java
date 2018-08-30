package cc.iliz.mybatis.shading.strategy;

import cc.iliz.mybatis.shading.sqltable.SqlTableParser;

public interface TableStrategy {
	
	/**
	 * 获取分表策略
	 * @param parser sql parser
	 * @param tableName orginal table name
	 * @param param sql param
	 * @return converted table name
	 */
	String getShadeTableName(SqlTableParser parser,String tableName,Object param);
}
