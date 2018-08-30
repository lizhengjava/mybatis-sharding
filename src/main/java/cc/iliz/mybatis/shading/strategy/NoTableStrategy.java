package cc.iliz.mybatis.shading.strategy;

import cc.iliz.mybatis.shading.sqltable.SqlTableParser;

public class NoTableStrategy implements TableStrategy {

	@Override
	public String getShadeTableName(SqlTableParser parser,String tableName, Object param) {
		return tableName;
	}

}
