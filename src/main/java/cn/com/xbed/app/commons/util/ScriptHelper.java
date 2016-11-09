package cn.com.xbed.app.commons.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.logging.LogFactory;

import cn.com.xbed.app.commons.exception.ExceptionHandler;

public class ScriptHelper {
	private static ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ScriptHelper.class));

	private static ScriptEngine se = new ScriptEngineManager().getEngineByName("JavaScript");
	
	/**
	 * 传入运算表达式
	 * @param calcExpress 运算表达式
	 * @return
	 */
	public static Double calculate(String calcExpress) {
		try {
			return (Double) se.eval(calcExpress);
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		}
	}
}
