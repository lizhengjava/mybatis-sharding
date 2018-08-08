package cc.iliz.mybatis.shading.convert;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

import cc.iliz.mybatis.shading.sqltable.DefaultSqlTableParserFactory;
import cc.iliz.mybatis.shading.sqltable.SqlTableParserFactory;

public class BaseSqlConverter implements SqlConverter {

	@Override
	public String convert(String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
		sql = getSqlTableParserFactory().getSqlTableParser().markShardingTable(sql, parameterObject, parameterMappings);
		return sql;
	}
	
	/**
	 * 可以通过实现自定义的SqlTableParserFactory来实现不同的Sql处理方式。
	 * @return
	 */
	protected SqlTableParserFactory getSqlTableParserFactory(){
		return new DefaultSqlTableParserFactory();
	}

}
