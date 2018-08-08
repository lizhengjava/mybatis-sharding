package cc.iliz.mybatis.shading.strategy;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

import cc.iliz.mybatis.shading.sqltable.SqlTableParser;

public interface TableStrategy {
	/**
	 * 获取分表策略
	 * @param tableName
	 * @param param
	 * @param parameterMappings
	 * @return
	 */
	String getShadeTableName(SqlTableParser parser,String tableName,Object param,List<ParameterMapping> parameterMappings);
}
