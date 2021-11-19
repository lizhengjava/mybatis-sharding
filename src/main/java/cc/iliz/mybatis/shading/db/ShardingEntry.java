package cc.iliz.mybatis.shading.db;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.util.StringUtils;

/**
 * 分表实体类
 * @author lizhengjava
 *
 */
public class ShardingEntry {
	private Map<String,Set<String>> dbTables=new ConcurrentHashMap<>();
	private String sql;
	private SqlCommandType sqlCommandType;
	private Set<String> names;
	
	public ShardingEntry(){}
	
	public ShardingEntry(String sql) {
		super();
		this.sql = sql;
	}
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}

	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}

	public void setSqlCommandType(SqlCommandType sqlCommandType) {
		this.sqlCommandType = sqlCommandType;
	}

	public Set<String> getNames() {
		return names;
	}

	public void setNames(Set<String> names) {
		this.names = names;
	}

	public Map<String, Set<String>> getDbTables() {
		return dbTables;
	}

	public void setDbTables(Map<String, Set<String>> dbTables) {
		this.dbTables = dbTables;
	}
	
	public void addItemToDbTables(String datasourceName,String tableName){
		if(StringUtils.isEmpty(datasourceName) || StringUtils.isEmpty(tableName)){
			return;
		}
		Set<String> tableNames = dbTables.get(datasourceName);
		if(tableNames != null){
			tableNames.add(tableName);
		}else{
			tableNames = new HashSet<>();
			tableNames.add(tableName);
			dbTables.put(datasourceName, tableNames);
		}
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "ShardingEntry{" +
				"dbTables=" + dbTables +
				", sql='" + sql + '\'' +
				", sqlCommandType=" + sqlCommandType +
				", names=" + names +
				'}';
	}
}
