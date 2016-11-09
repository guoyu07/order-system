package cn.com.xbed.app.commons.util;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;


public class BeanUtilTool {
	public static Map<String, Object> getBeanPropertyMap(Object bean) {
		try {
			return PropertyUtils.describe(bean);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object getBeanPropertyValue(Object bean, String property) {
		try {
			return PropertyUtils.getProperty(bean, property);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
