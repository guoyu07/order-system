package cn.com.xbed.app.commons.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesReader {
	public static final ConcurrentHashMap<String, String> consvars = new ConcurrentHashMap<String, String>();

	static {
		initConstVars("/constvar.properties");
	}

	// 读取properties文件
	public static Properties readProperties(String propFile) {
		Properties properties = new Properties();
		try {
			InputStream in = PropertiesReader.class.getResourceAsStream(propFile);
			properties.load(in);
		} catch (Exception e) {
			properties = null;
		}
		return properties;
	}

	public static void initConstVars() {
		initConstVars("/constvar.properties");
	}

	// 初始化和刷新
	public static void initConstVars(String path) {
		try {
			consvars.clear();
			Properties prop = readProperties(path);
			Enumeration<String> enumNames = (Enumeration<String>) prop.propertyNames();// #注释的会被忽略
			while (enumNames.hasMoreElements()) {
				String key = enumNames.nextElement();
				String value = prop.getProperty(key);
				value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
				consvars.put(key, value);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	
	public static void printConst() throws Exception {
		Iterator it = consvars.keySet().iterator();
		for (; it.hasNext();) {
			String key = (String) it.next();
			String value = consvars.get(key);
		}
	}
}
