package cc.iliz.mybatis.shading.convert;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

public interface SqlConverter extends Converter {

	String convert(String sql,List<ParameterMapping> parameterMappings,Object parameterObject);
}
