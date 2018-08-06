package cc.iliz.mybatis.shading.sqltable;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

public interface SqlTableParser {

	String markShardingTable(String sql,Object param,List<ParameterMapping> parameterMappings);
}
