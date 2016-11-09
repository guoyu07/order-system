package cn.com.xbed.app.commons.exception;

import org.apache.commons.logging.Log;

public class ExceptionHandler {

	private Log log;

	public ExceptionHandler(Log log) {
		this.log = log;

	}

	/**
	 * DAO层
	 * 
	 * @param e
	 * @return
	 */
	public RuntimeException logDaoException(Exception e) {
		// 设计的艺术在于,底层的就不要打印、不要记录日志,包装给顶层进行打印和记日志,否则会产生很多重复的日志
		// printStackTrace(e);
		// commonLog(log, e);
		return new DaoException(e);
	}

	public RuntimeException logDaoException(String message, Exception e) {
		return new DaoException(message, e);
	}

	/**
	 * 业务逻辑层
	 * 
	 * @param e
	 * @return
	 */
	public RuntimeException logServiceException(Exception e) {
		log.error("", e);
		return new ServiceException(e);
	}

	public RuntimeException logServiceException(String message, Exception e) {
		log.error(message, e);
		return new ServiceException(message, e);
	}

	/**
	 * 工具类
	 * 
	 * @param e
	 * @return
	 */
	public RuntimeException logToolException(Exception e) {
		log.error("", e);
		return new RuntimeException(e);
	}

	public RuntimeException logToolException(String message, Exception e) {
		log.error(message, e);
		return new RuntimeException(message, e);
	}

	/**
	 * action层, 顶层
	 * 
	 * @param e
	 * @return
	 */
	public RuntimeException logActionException(Exception e) {
		// e.printStackTrace();//log.error已经配置了输出到控制台,不再重复打印
		log.error("", e);// 不能使用log.error(e),否则堆栈信息打印不出来
		return new ControllerException(e);
	}

	public RuntimeException logActionException(String message, Exception e) {
		// e.printStackTrace();//log.error已经配置了输出到控制台,不再重复打印
		log.error(message, e);
		return new ControllerException(message, e);
	}

	/**
	 * 该方法仅由service层调用,在dao层调用没有意义,errorcode是为了区分业务的
	 * @param errorCode
	 * @param errorMsg
	 * @param errorVal
	 * @return
	 */
	public ErrorCodeException newErrorCodeException(String errorCode, String errorMsg, Object errorVal) {
		return new ErrorCodeException(errorCode, errorMsg, errorVal);
	}
	
	public ErrorCodeException newErrorCodeException(String errorCode, String errorMsg) {
		this.getLog().error("ErrorCodeException,errorCode:" + errorCode + ",errorMsg:" + errorMsg);
		return newErrorCodeException(errorCode, errorMsg, null);
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}
}
