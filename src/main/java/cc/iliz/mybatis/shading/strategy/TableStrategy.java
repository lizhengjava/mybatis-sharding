package cc.iliz.mybatis.shading.strategy;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

public interface TableStrategy {
	/**
	 * 获取分表策略
	 * @param tableName
	 * @param param
	 * @param parameterMappings
	 * @return
	 */
	String getShadeTableName(String tableName,Object param,List<ParameterMapping> parameterMappings);
}
