package cc.iliz.mybatis.shading.sqltable;

import cc.iliz.mybatis.shading.db.ShardingEntry;

public interface SqlTableParser {

	ShardingEntry markShardingTable(String sql, Object param);
}
