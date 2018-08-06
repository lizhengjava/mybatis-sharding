package cc.iliz.mybatis.shading.sqltable;

import java.util.List;
import java.util.Locale;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;

public class RouteSqlTableParser implements SqlTableParser {
	private static final Log log = LogFactory.getLog(RouteSqlTableParser.class);

	@Override
	public String markShardingTable(String sql,Object param,List<ParameterMapping> parameterMappings) {
		SqlCommandType commandType = getSqlCommandType(sql);
		BaseSqlTableParser sqlTableParser = null;
		switch(commandType){
		case SELECT:
			sqlTableParser = new SelectSqlTableParser();
			break;
		case UPDATE:
			sqlTableParser = new UpdateSqlTableParser();
			break;
		case DELETE:
			sqlTableParser = new DeleteSqlTableParser();
			break;
		case INSERT:
			sqlTableParser = new InsertSqlTableParser();
			break;
		case UNKNOWN:
		case FLUSH:
		}
		if(log.isDebugEnabled()){
			log.debug("find table sharding sql [" + sql + "]'s parser is [" +sqlTableParser.getClass().getName()+"]");
		}
		if(sqlTableParser != null){
			sql = sqlTableParser.markShardingTable(sql, param, parameterMappings);
		}
		
		return sql;
	}
	
	
	private SqlCommandType getSqlCommandType(String sql){
		String usql = sql.trim().toUpperCase(Locale.ENGLISH);
		if(usql.startsWith("SELECT")){
			return SqlCommandType.SELECT;
		}else if(usql.startsWith("UPDATE")){
			return SqlCommandType.UPDATE;
		}else if(usql.startsWith("DELETE")){
			return SqlCommandType.DELETE;
		}else if(usql.startsWith("INSERT")){
			return SqlCommandType.INSERT;
		}else{
			return SqlCommandType.UNKNOWN;
		}
	}
}
