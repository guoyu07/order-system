package cn.com.xbed.app.commons.util;

import java.util.Map;

import cn.com.xbed.app.commons.exception.ErrorCodeException;

public class RestCallHelper {
	public static final String RETCODE_KEY = "retcode";
	public static final String RETVAL_KEY = "retval";
	public static final String RETMSG_KEY = "retmsg";
	
	public static void fillSuccessParams(Map<String, Object> resultMap, Object retval) {
		resultMap.put(RETCODE_KEY, "1");
		resultMap.put(RETVAL_KEY, retval);
		resultMap.put(RETMSG_KEY, "成功");
	}

	public static void fillFailParams(Map<String, Object> resultMap) {
		resultMap.put(RETCODE_KEY, "-1");
		resultMap.put(RETVAL_KEY, null);
		resultMap.put(RETMSG_KEY, "失败");
	}
	
	public static void fillFailParams(Map<String, Object> resultMap, Object message) {
		resultMap.put(RETCODE_KEY, "-1");
		resultMap.put(RETVAL_KEY, null);
		resultMap.put(RETMSG_KEY, message);
	}
	
	public static void fillParams(Map<String, Object> resultMap, Object retcode, Object retval, Object message) {
		resultMap.put(RETCODE_KEY, retcode+"");
		resultMap.put(RETVAL_KEY, retval);
		resultMap.put(RETMSG_KEY, message);
	}

	public static void fillExceptionParams(Map<String, Object> resultMap, Exception e) {
		ErrorCodeException ece = getErrorCodeException(e);
		if (ece == null) {
			resultMap.put(RETCODE_KEY, "-99");
			resultMap.put(RETVAL_KEY, null);
			resultMap.put(RETMSG_KEY, e.getMessage());
		} else {
			resultMap.put(RETCODE_KEY, ece.getErrorCode());
			resultMap.put(RETVAL_KEY, ece.getErrorVal());
			resultMap.put(RETMSG_KEY, ece.getErrorMsg());
		}
		
	}
	
	// 递归获取异常链
	public static ErrorCodeException getErrorCodeException(Throwable e) {
		if (e == null) {
			return null;
		}
		if (e instanceof ErrorCodeException) {
			return (ErrorCodeException) e;
		} else {
			return getErrorCodeException(e.getCause());
		}
	}
}
