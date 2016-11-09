package cn.com.xbed.app.commons.util;

public class EnumHelper {
	public static String getEnumString(Enum enumObj) {
		return enumObj == null ? "null" : enumObj.toString();
	}
	
	
	public static String getEnumStringNotNull(Enum enumObj) {
		if (enumObj == null) {
			throw new RuntimeException("传入的枚举类为null: " + enumObj);
		}
		return enumObj.toString();
	}
}


