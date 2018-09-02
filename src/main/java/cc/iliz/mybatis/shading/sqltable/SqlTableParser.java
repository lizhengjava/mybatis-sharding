package cc.iliz.mybatis.shading.sqltable;

import org.apache.ibatis.mapping.SqlCommandType;

import cc.iliz.mybatis.shading.db.ShardingEntry;

public interface SqlTableParser {

	ShardingEntry markShardingTable(String sql, Object param);
	
	SqlCommandType getSqlCommandType();
}
