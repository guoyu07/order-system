package cn.com.xbed.app.commons.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public class StringSimpleHelper {
	public static String getFixDigiNum(int num, int digi) {
		String result = num + "";
		while (result.length() < digi) {
			result = "0" + result;
		}
		return result;
	}
	
	
	
}
