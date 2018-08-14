package cc.iliz.mybatis.shading.convert;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

public interface SqlConverter extends Converter {

	/**
	 * convert sql accordig strategy
	 * @param sql original sql
	 * @param parameterMappings param mapping
	 * @param parameterObject params
	 * @return converted sql
	 */
	String convert(String sql,List<ParameterMapping> parameterMappings,Object parameterObject);
}
