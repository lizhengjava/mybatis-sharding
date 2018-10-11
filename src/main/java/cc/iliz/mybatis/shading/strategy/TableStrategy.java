package cc.iliz.mybatis.shading.strategy;

import cc.iliz.mybatis.shading.sqltable.SqlTableParser;

public interface TableStrategy {
	
	/**
	 * 获取分表策略
	 * @param parser sql解析器
	 * @param tableName 原sql表名
	 * @param param sql sql中的参数
	 * @return 转换后表名
	 */
	default String getShadeTableName(SqlTableParser parser,String tableName,Object param){
		return tableName;
	}
}
