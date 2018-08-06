package cc.iliz.mybatis.shading.sqltable;

import java.util.regex.Pattern;

public class InsertSqlTableParser extends BaseSqlTableParser {

	@Override
	public Pattern getRegPattern() {
		return Pattern.compile("insert\\s+into\\s+(\\w+)",Pattern.CASE_INSENSITIVE);
	}

}
