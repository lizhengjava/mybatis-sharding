package cc.iliz.mybatis.shading.strategy;

import cc.iliz.mybatis.shading.db.ShardingEntry;

public interface DbStrategy {

	public String getShardingDataSource(ShardingEntry shardingEntry);
}
