package cn.com.xbed.app.commons.util;

import java.util.Random;
import java.util.UUID;

public class RandomUtil {
	public static String getUUID36Lowercase() {
		return UUID.randomUUID().toString();
	}
	
	public static String getUUID32Lowercase() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String getUUID36Uppercase() {
		return UUID.randomUUID().toString().toUpperCase();
	}
	
	public static String getUUID32Uppercase() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}
	/**
	 * 
	 * @param digi 正整数，想要获取多少位的数字字符
	 * @return
	 */
	public static String getRandomStr(int digi) {
		if (digi <= 0) {
			throw new RuntimeException("getRandomStr入参错误 digi:" + digi);
		}
		StringBuffer buf = new StringBuffer();
		Random r = new Random();
		for (int i = 0; i < digi; i++) {
			buf.append(r.nextInt(10));
		}
		return buf.toString();
	}
	
	public static String getBookOrderNo() {
		return DateUtil.getCurTimestampStr() + RandomUtil.getRandomStr(3) + "01";
	}
	public static String getCleanOrderNo() {
		return DateUtil.getCurTimestampStr() + RandomUtil.getRandomStr(3) + "02";
	}
	public static String getStopOrderNo() {
		return DateUtil.getCurTimestampStr() + RandomUtil.getRandomStr(3) + "03";
	}
}
