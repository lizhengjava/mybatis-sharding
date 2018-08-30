package cc.iliz.mybatis.shading.sqltable;

public class SqlTableParserFactory {
	private static SqlTableParserFactory instance= new SqlTableParserFactory();
	
	private SqlTableParserFactory(){}
	
	public static SqlTableParserFactory getInstance(){
		return instance;
	}
	
	public SqlTableParser getSqlTableParser(){
		return new RouteSqlTableParser();
	}
}
