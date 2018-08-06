package cc.iliz.mybatis.shading.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ShardingConfigEntityResolver implements EntityResolver {
	private static final Map<String, String> doctypeMap = new HashMap<String, String>();
	
	private static final String SHARDING_CONFIG_DTD = "cc/iliz/mybatis/shading/parse/mybatis-sharding-config.dtd";
	
	private static final String SHARDING_CONFIG_PUBLIC = "-//mybatisSharding.iliz.cc//DTD mybatisSharding 1.0//EN".toUpperCase(Locale.ENGLISH);
	private static final String SHARDING_CONFIG_SYSTEM = "http://mybatisSharding.iliz.cc/dtd/mybatis-sharding-config.dtd".toUpperCase(Locale.ENGLISH);

	static{
		doctypeMap.put(SHARDING_CONFIG_SYSTEM, SHARDING_CONFIG_DTD);
	    doctypeMap.put(SHARDING_CONFIG_PUBLIC, SHARDING_CONFIG_DTD);

	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if(publicId != null){
			publicId = publicId.toUpperCase(Locale.ENGLISH);
		}
		if(systemId != null){
			systemId = systemId.toUpperCase(Locale.ENGLISH);
		}
		
		InputSource source = null;
		try {
			String path = doctypeMap.get(publicId);
			source = getInputSource(path,source);
			if(source == null){
				path = doctypeMap.get(systemId);
				source = getInputSource(path,source);
			}
		} catch (Exception e) {
			throw new SAXException(e.toString());
		}
		
		return source;
	}

	  private InputSource getInputSource(String path, InputSource source) {
	    if (path != null) {
	      InputStream in;
	      try {
	        in = Resources.getResourceAsStream(path);
	        source = new InputSource(in);
	      } catch (IOException e) {
	        // ignore, null is ok
	      }
	    }
	    return source;
	  }
}
