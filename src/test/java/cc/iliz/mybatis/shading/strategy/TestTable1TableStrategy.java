package cc.iliz.mybatis.shading.strategy;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import cc.iliz.mybatis.shading.annotation.Strategy;
import cc.iliz.mybatis.shading.sqltable.SqlTableParser;

@Strategy(tableName="app_test")
public class TestTable1TableStrategy implements TableStrategy {

	@Override
	public String getShadeTableName(SqlTableParser parser,String tableName, Object param, List<ParameterMapping> parameterMappings) {
		if(param != null){
			MetaObject object = SystemMetaObject.forObject(param);
			int id = Integer.parseInt(object.getValue("id").toString());
			StringBuffer sb = new StringBuffer();
			sb.append(tableName).append("_").append(id%5);
			return sb.toString();
		}
		return tableName;
	}

}
