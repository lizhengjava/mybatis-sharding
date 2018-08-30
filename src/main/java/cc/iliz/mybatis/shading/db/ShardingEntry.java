package cc.iliz.mybatis.shading.db;

public class ShardingEntry {
	private ShardingProxyDataSource proxy;
	private String sql;
	
	public ShardingEntry(){}
	
	public ShardingEntry(ShardingProxyDataSource proxy, String sql) {
		super();
		this.proxy = proxy;
		this.sql = sql;
	}
	
	public ShardingProxyDataSource getProxy() {
		return proxy;
	}
	public void setProxy(ShardingProxyDataSource proxy) {
		this.proxy = proxy;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
