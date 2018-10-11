package cc.iliz.mybatis.shading.sqltable;

import java.util.regex.Pattern;

public class SelectSqlTableParser extends BaseSqlTableParser {

	@Override
	public Pattern getRegPattern() {
		return Pattern.compile("from\\s+((\\w+)+,?)+|join\\s+(\\w+)",Pattern.CASE_INSENSITIVE);
	}

}
