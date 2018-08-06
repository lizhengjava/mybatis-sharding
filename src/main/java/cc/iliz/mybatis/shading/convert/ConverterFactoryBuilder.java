package cc.iliz.mybatis.shading.convert;

public class ConverterFactoryBuilder {
	private ConverterFactory converterFactory = null;
	private static ConverterFactoryBuilder builder = null; 
	
	private ConverterFactoryBuilder(){}
	
	public static ConverterFactoryBuilder getConverterFactoryBuilder(){
		if(builder == null){
			builder = new ConverterFactoryBuilder();
		}
		return builder;
	}
	
	public ConverterFactory getConverterFacotry(){
		if(converterFactory == null){
			converterFactory = new DefaultConverterFacotry();
		}
		return converterFactory;
	}
	
	public void setConverterFactory(ConverterFactory converterFactory){
		this.converterFactory = converterFactory;
	}

}
