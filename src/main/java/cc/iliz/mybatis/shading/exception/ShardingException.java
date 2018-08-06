package cc.iliz.mybatis.shading.exception;

public class ShardingException extends Exception {

	private static final long serialVersionUID = 875322428408033600L;
	
	public ShardingException() {
		super();
	}

	public ShardingException(String msg) {
		super(msg);
	}

	public ShardingException(String msg, Throwable t) {
		super(msg, t);
	}

	public ShardingException(Throwable t) {
		super(t);
	}
}
