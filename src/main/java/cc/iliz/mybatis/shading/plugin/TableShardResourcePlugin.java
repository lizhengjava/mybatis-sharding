package cc.iliz.mybatis.shading.plugin;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.context.ApplicationContext;

import cc.iliz.mybatis.shading.db.ShardingProxyDataSource;
import cc.iliz.mybatis.shading.strategy.ShardResourceStrategy;

@Intercepts({@Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
	@Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }),
	@Signature(type = StatementHandler.class, method = "queryCursor", args = { Statement.class }),
	@Signature(type = StatementHandler.class, method = "query", args = { Statement.class,ResultHandler.class })})
public class TableShardResourcePlugin implements Interceptor {
	private static final Log log = LogFactory.getLog(TableShardResourcePlugin.class);
	private ApplicationContext applicationContext;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object o = null;
		try{
			o = invocation.proceed();
		}catch(Exception e){
			log.error("--TableShardResourcePlugin error [" + e.getMessage() + "]");
			Transaction tx = ShardingProxyDataSource.getThreadLocalTransaction();
			if(tx != null){
				tx.rollback();
				ShardingProxyDataSource.removeThreadLocalTransaction();
				tx.close();
			}
			
			//custom cancel
			ShardResourceStrategy resourceStrategy = null;
			try{
				resourceStrategy = applicationContext.getBean(ShardResourceStrategy.class);
			}catch(Exception ex){
				log.debug("业务系统无资源策略实现");
			}
			if(resourceStrategy != null){
				resourceStrategy.resourceCancel();
			}
		}finally{
			Transaction tx = ShardingProxyDataSource.getThreadLocalTransaction();
			if(tx != null){
				tx.commit();
				ShardingProxyDataSource.removeThreadLocalTransaction();
				tx.close();
				if (log.isDebugEnabled()) {
					log.debug("--Mybatis sharding Connection closed [" + tx.getClass() + "]");
				}
			}
			
			//custom confirm
			ShardResourceStrategy resourceStrategy = null;
			try{
				resourceStrategy = applicationContext.getBean(ShardResourceStrategy.class);
			}catch(Exception ex){
				log.debug("业务系统无资源策略实现");
			}
			if(resourceStrategy != null){
				resourceStrategy.resourceConfirm();
			}
		}
		
		return o;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
