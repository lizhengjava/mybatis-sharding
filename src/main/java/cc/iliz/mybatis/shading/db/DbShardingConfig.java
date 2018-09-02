package cc.iliz.mybatis.shading.db;

import java.util.Map;

import org.springframework.util.StringUtils;

import cc.iliz.mybatis.shading.util.Constants;

public class DbShardingConfig {
	/**
	 * 多数据源属性，属性key是数据源的名称，value包含数据源的优先级与只读属性，配置格式为：order:isRead，order使用数字，isRead是布尔值，1表示true,0表示false。
	 */
	private Map<String,String> dbProp;
	private String defaultDB ;
	
	public Map<String, String> getDbProp() {
		return dbProp;
	}
	public void setDbProp(Map<String, String> dbProp) {
		this.dbProp = dbProp;
	}
	public String getDefaultDB() {
		return defaultDB;
	}
	public void setDefaultDB(String defaultDB) {
		this.defaultDB = defaultDB;
	}
	
	public int getDbOrder(String dbname){
		String[] props = splitDbProp(dbProp.get(dbname));
		if(props != null && props.length == 2){
			try{
				return Integer.valueOf(props[0]);
			}catch(Exception e){
				return Constants.DEFAULT_SHARDING_DB_ORDER;
			}
		}
		return Constants.DEFAULT_SHARDING_DB_ORDER;
	}
	
	public boolean isDbOnlyRead(String dbname){
		String[] props = splitDbProp(dbProp.get(dbname));
		if(props != null && props.length == 2){
			return Boolean.valueOf(props[1]);
		}
		return false;
	}
	
	private String[] splitDbProp(String prop){
		if(StringUtils.hasText(prop)){
			String[] props = prop.split(":");
			if(props.length == 2){
				return props;
			}else if(props.length > 2){
				return new String[]{props[0],props[1]};
			}
			
		}
		return null;
	}
}
