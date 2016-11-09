package cn.com.xbed.app.commons.exception;

/**
 * 带错误码的异常
 * 
 * @author Administrator
 *
 */
public final class ErrorCodeException extends RuntimeException {
	private String errorCode;
	private String errorMsg;
	private Object errorVal;

	// 只对同包的ExceptionHandler暴露
	protected ErrorCodeException(String errorCode, String errorMsg, Object errorVal) {
		super(errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.errorVal = errorVal;
	}

	public String getErrorCode() {
		return errorCode;
	}

	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	private void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Object getErrorVal() {
		return errorVal;
	}

	private void setErrorVal(Object errorVal) {
		this.errorVal = errorVal;
	}

}
