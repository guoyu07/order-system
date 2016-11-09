package cn.com.xbed.app.commons.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonHelper {
	public static class JSONEntity {

		public JSONEntity(Object value) {
			if (value instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) value;
				this.size = jsonArray.size();
			} else {
				this.size = 1;
			}
			this.value = value;
		}

		private int size;
		private Object value;

		public int getSize() {
			return size;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 解析JSON字符串成为一个Object，结果可能是JSONArray(多个)或JSONObject(单个)
	 * 
	 * @param jsonStr
	 *            要解析的JSON字符串
	 * @return
	 */
	public static JSONEntity parseJSONStr2JSONEntity(String jsonStr) {
		try {
			return new JSONEntity(JSON.parse(jsonStr));
		} catch (Exception e) {
			throw new RuntimeException("JSON格式错误: " + jsonStr, e);
		}
	}

	public static JSONObject parseJSONStr2JSONObject(String jsonStr) {
		try {
			return (JSONObject) JSONObject.parse(jsonStr);
		} catch (Exception e) {
			throw new RuntimeException("JSON格式错误: " + jsonStr, e);
		}
	}

	public static JSONArray parseJSONStr2JSONArray(String jsonStr) {
		try {
			return (JSONArray) JSONArray.parse(jsonStr);
		} catch (Exception e) {
			throw new RuntimeException("JSON格式错误: " + jsonStr, e);
		}
	}

	public static <T> T parseJSONStr2T(String jsonStr, Class<T> clazz) {
		try {
			return JSONObject.parseObject(jsonStr, clazz);
		} catch (Exception e) {
			throw new RuntimeException("JSON格式错误: " + jsonStr, e);
		}
	}

	public static <T> List<T> parseJSONStr2TList(String jsonStr, Class<T> clazz) {
		try {
			return JSONObject.parseArray(jsonStr, clazz);
		} catch (Exception e) {
			throw new RuntimeException("JSON格式错误: " + jsonStr, e);
		}
	}

	/**
	 * 将对象转换成JSON字符串，该对象也可以是集合
	 * 
	 * @param obj
	 * @return [{"userId":1},{"userId":2}]或{"userId":1}
	 */
	public static String toJSONString(Object obj) {
		try {
			return JSONObject.toJSONString(obj);
		} catch (Exception e) {
			throw new RuntimeException("对象转换成JSON字符串出错[" + obj + "]", e);
		}
	}
	
	/**
	 * 格式化时间为yyyy-MM-dd HH:mm:ss格式
	 * @param obj
	 * @return
	 */
	public static String toJSONStringWithFormatDate(Object obj) {
		try {
			return JSONObject.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
		} catch (Exception e) {
			throw new RuntimeException("对象转换成JSON字符串出错[" + obj + "]", e);
		}
	}
	
	/**
	 * 将对象转换成JSON字符串, 所有的字段会被用字符串替代,例如int类型和boolean会呗转成字符串形式
	 * @param obj
	 * @return
	 */
	public static String toJSONStringAllFieldsString(Object obj) {
		try {
			return JSONObject.toJSONString(obj);
		} catch (Exception e) {
			throw new RuntimeException("对象转换成JSON字符串出错[" + obj + "]", e);
		}
	}

}
