package cc.iliz.mybatis.shading.strategy;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

import cc.iliz.mybatis.shading.sqltable.SqlTableParser;

public class BaseTableStrategy implements TableStrategy {

	@Override
	public String getShadeTableName(SqlTableParser parser,String tableName, Object param, List<ParameterMapping> parameterMappings) {
		// TODO Auto-generated method stub
		return null;
	}

}
