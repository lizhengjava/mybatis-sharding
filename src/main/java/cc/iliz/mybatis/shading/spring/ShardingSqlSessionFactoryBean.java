package cc.iliz.mybatis.shading.spring;

import java.io.IOException;
import java.util.Properties;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import cc.iliz.mybatis.shading.plugin.TableShardPlugin;
import cc.iliz.mybatis.shading.plugin.TableShardResourcePlugin;

/**
 * ShardingSqlSessionFactoryBean是带有分表功能的sqlSessionFactory，在与spring的整合中只需要将{@code SqlSessionFactoryBean} 
 * 的配置替换为此类即可，此类提供了shardingScanPackage和tableShardPlugin两个属性可以设置，tableShardPlugin提供自定义分表组件注入功能，
 * shardingScanPackage提供初始化路径扫描功能，推荐使用spring{@code Component}注解扫描自定义{@code TableStrategy}}。
 * 
 * Configuration sample:
 *
 * <pre class="code">
 * {@code
 * <bean id="sqlSessionFactory" class="cc.iliz.mybatis.shading.spring.ShardingSqlSessionFactoryBean">
 *        <property name="dataSource" ref="dataSource" />
 *        <property name="configLocation" value="classpath:/mybatis/mybatis-config.xml" />
 *        <property name="mapperLocations" value="classpath:mybatis/* /*.xml" />
 *        <property name="shardingScanPackage" value="com.iliz" />
 *    </bean>
 * }
 * </pre>
 * 
 * @author lizhengjava
 *
 */
public class ShardingSqlSessionFactoryBean extends SqlSessionFactoryBean implements ApplicationContextAware {
	private static final Log log = LogFactory.getLog(ShardingSqlSessionFactoryBean.class);

	private String shardingScanPackage;
	private TableShardPlugin tableShardPlugin = new TableShardPlugin();
	private TableShardResourcePlugin tableShardResourcePlugin = new TableShardResourcePlugin();
	private ApplicationContext applicationContext;

	public String getShardingScanPackage() {
		return shardingScanPackage;
	}

	public void setShardingScanPackage(String shardingScanPackage) {
		this.shardingScanPackage = shardingScanPackage;
	}

	public TableShardPlugin getTableShardPlugin() {
		return tableShardPlugin;
	}

	public void setTableShardPlugin(TableShardPlugin tableShardPlugin) {
		this.tableShardPlugin = tableShardPlugin;
	}

	@Override
	protected SqlSessionFactory buildSqlSessionFactory() throws Exception {
		SqlSessionFactory sqlSessionFactory = super.buildSqlSessionFactory();
		if (StringUtils.hasText(shardingScanPackage)) {
			Properties prop = new Properties();
			prop.put(TableShardPlugin.STRATEGY_CONFIG, shardingScanPackage);
			tableShardPlugin.setProperties(prop);

			if (log.isDebugEnabled()) {
				log.debug("shardingScanPackage is [" + shardingScanPackage + "]");
			}
		}
		tableShardPlugin.setApplicationContext(this.applicationContext);
		tableShardResourcePlugin.setApplicationContext(this.applicationContext);
		
		sqlSessionFactory.getConfiguration().addInterceptor(tableShardPlugin);
		sqlSessionFactory.getConfiguration().addInterceptor(tableShardResourcePlugin);
		return sqlSessionFactory;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
