package cc.iliz.mybatis.shading.strategy;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

public class NoTableStrategy implements TableStrategy {

	@Override
	public String getShadeTableName(String tableName, Object param, List<ParameterMapping> parameterMappings) {
		return tableName;
	}

}
