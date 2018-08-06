package cc.iliz.mybatis.shading.sqltable;

public class DefaultSqlTableParserFactory implements SqlTableParserFactory {
	
	@Override
	public SqlTableParser getSqlTableParser(){
		return new RouteSqlTableParser();
	}
	
}
