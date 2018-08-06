package cc.iliz.mybatis.shading.convert;

public interface ConverterFactory {
	public void registConvert(Class<Converter> clazz,Converter converter);
	public Converter getConverter();
	public Converter getConverter(Class<Converter> clazz);
	public SqlConverter getSqlConverter();
}
