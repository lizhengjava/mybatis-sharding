package cc.iliz.mybatis.shading.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DbShardingConnectionProxy implements InvocationHandler {
	private Object target;
	
	public DbShardingConnectionProxy(Object target){
		this.target = target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//		System.out.println("+++++++++++++++++++++++++++++++++DbShardingConnectionProxy" );
		return method.invoke(target, args);
	}

}
