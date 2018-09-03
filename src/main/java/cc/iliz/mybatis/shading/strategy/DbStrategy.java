package cc.iliz.mybatis.shading.strategy;

import cc.iliz.mybatis.shading.db.ShardingEntry;

/**
 * 数据库分库策略，实现后用{@code Component}组件注册到spring中
 * @author lizhengjava
 *
 */
public interface DbStrategy {

	/**
	 * 自定义数据库分表策略
	 * @param shardingEntry 分表策略参数
	 * @return 数据源spring配置名称
	 */
	public String getShardingDataSource(ShardingEntry shardingEntry);
}
