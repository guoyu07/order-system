package cn.com.xbed.app.commons.exception;

import java.util.Collection;
import java.util.Map;

public class AssertHelper {

	// ****************** 常用 *******************
	/**
	 * 断言非null非空串非无效空白(blank) <br>
	 * 抛的情况: null、""、"  " <br>
	 * 不抛出的情况: "abc"、" abc "
	 * 
	 * @param text
	 */
	public static void notNullEmptyBlank(CharSequence... text) {
		for (int i = 0; i < text.length; i++) {
			notNullEmptyBlank(text[i],
					"[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
		}
	}

	public static void notNullEmptyBlank(CharSequence text, String message) {
		if (!isNotNullNotEmptyAfterTrim(text)) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void notNullEmptyBlank(String message, CharSequence ... text) {
		for (int i = 0; i < text.length; i++) {
			notNullEmptyBlank(text[i], message);
		}
	}
	
	
	public static void notZeroNotNegative(Number... nums) {
		for (int i = 0; i < nums.length; i++) {
			notZeroNotNegative(nums[i],
					"[Assertion failed] - this Number argument must be positive number; it must not be negative or zero");
		}
	}

	public static void notZeroNotNegative(Number num, String message) {
		if (isZeroOrNegative(num)) {
			throw new IllegalArgumentException(message);
		}
	}
	
	

	/**
	 * 
	 * 抛的情况: null、""、 <br>
	 * 不抛出的情况: "  "、"abc"、" abc "
	 * 
	 * @param str
	 */
	public static void notNullNotEmpty(CharSequence... str) {
		for (int i = 0; i < str.length; i++) {
			notNullNotEmpty(str[i],
					"[Assertion failed] - this String argument must have length; it must not be null or empty");
		}
	}

	public static void notNullNotEmpty(CharSequence str, String message) {
		if (isNullOrEmpty(str)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 断言非空<br>
	 * 抛的情况: null <br>
	 * 
	 * @param object
	 */
	public static void notNull(Object... object) {
		for (int i = 0; i < object.length; i++) {
			notNull(object[i], "[Assertion failed] - this argument is required; it must not be null");
		}
	}
	
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void notNull(String message, Object ... object) {
		for (int i = 0; i < object.length; i++) {
			notNull(object[i], message);
		}
	}
	
	/**
	 * 判断集合 一般判断List、Set等集合对象<br>
	 * 抛的情况: null或size是0
	 * 
	 * @param collection
	 */
	public static void notEmpty(Collection<?> collection) {
		notEmpty(collection,
				"[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
	}

	public static void notEmpty(Collection<?> collection, String message) {
		if (collection == null || collection.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 判断数组<br>
	 * 抛的情况: null或长度是0
	 * 
	 * @param array
	 */
	public static void notEmpty(Object[] array) {
		notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
	}

	public static void notEmpty(Object[] array, String message) {
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 判断Map<br>
	 * 抛的情况: null或长度是0
	 * 
	 * @param array
	 */
	public static void notEmpty(Map<?, ?> map) {
		notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
	}

	public static void notEmpty(Map<?, ?> map, String message) {
		if (map == null || map.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 断言是true
	 * 
	 * @param expression
	 */
	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isFalse(boolean expression) {
		isFalse(expression, "[Assertion failed] - this expression must be false");
	}

	public static void isFalse(boolean expression, String message) {
		if (expression) {
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * 判断数组没有null元素
	 * 
	 * @param array
	 */
	public static void noNullElements(Object[] array) {
		noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
	}

	public static void noNullElements(Object[] array, String message) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}

	/**
	 * 断言obj能够赋值给clazz类型的类或接口
	 * 
	 * @param clazz
	 * @param obj
	 */
	public static void isInstanceOf(Class<?> clazz, Object obj) {
		isInstanceOf(clazz, obj, "");
	}

	public static void isInstanceOf(Class<?> type, Object obj, String message) {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			throw new IllegalArgumentException((!isNullOrEmpty(message) ? message + " " : "") + "Object of class ["
					+ (obj != null ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
		}
	}

	// **************** 不常用 *********************
	public static void isAssignable(Class<?> superType, Class<?> subType) {
		isAssignable(superType, subType, "");
	}

	public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
		notNull(superType, "Type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
		}
	}

	// ***************** 不向外暴露 ********************
	private static boolean isNullOrEmpty(CharSequence str) {
		return (str == null || str.length() == 0);
	}

	private static boolean isNotNullNotEmptyAfterTrim(CharSequence str) {
		if (isNullOrEmpty(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isZeroOrNegative(Number num) {
		if (num == null) {
			return false;
		}
		if (num instanceof Byte) {
			return num.byteValue() <= 0;
		} else if (num instanceof Short) {
			return num.shortValue() <= 0;
		} else if (num instanceof Integer) {
			return num.intValue() <= 0;
		} else if (num instanceof Long) {
			return num.longValue() <= 0;
		} else if (num instanceof Float) {
			return num.floatValue() <= 0;
		} else if (num instanceof Double) {
			return num.doubleValue() <= 0;
		} else {
			return num.doubleValue() <= 0;// double是容器最大的
		}
	}
}
