package cc.iliz.mybatis.shading.sqltable;

import java.util.regex.Pattern;

public class UpdateSqlTableParser extends BaseSqlTableParser {

	@Override
	public Pattern getRegPattern() {
		return Pattern.compile("update\\s+(\\w+)",Pattern.CASE_INSENSITIVE);
	}

}
