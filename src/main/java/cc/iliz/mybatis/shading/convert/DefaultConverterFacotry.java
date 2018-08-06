package cc.iliz.mybatis.shading.convert;

public class DefaultConverterFacotry implements ConverterFactory {

	public Converter getConverter(){
		return null;
	}

	@Override
	public void registConvert(Class<Converter> clazz, Converter converter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Converter getConverter(Class<Converter> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlConverter getSqlConverter() {
		return new BaseSqlConverter();
	}
}
