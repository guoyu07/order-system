package cn.com.xbed.app.commons.debug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.com.xbed.app.commons.util.ConsolePrinter;

public class ActionDebugInterceptor implements HandlerInterceptor {

	/**
	 * 该方法在目标方法之前被调用. 若返回值为 true, 则继续调用后续的拦截器和目标方法. 若返回值为 false,
	 * 则不会再调用后续的拦截器和目标方法.
	 * 
	 * 可以考虑做权限. 日志, 事务等.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		ConsolePrinter.println("~~~~~~~~~~~~~~~~Action运行之前...begin~~~~~~~~~~~~~~~~~~~~");
		HandlerMethod h = (HandlerMethod) handler;
		String fullQualifiedClassName = h.getBean().getClass().getName();
		String methodName = h.getMethod().getName();
		Class[] params = h.getMethod().getParameterTypes();
		String paramNames = "";
		for (int i = 0; i < params.length; i++) {
			if (i == params.length - 1) {
				paramNames += params[i].getName();
			} else {
				paramNames += params[i].getName() + ", ";
			}
		}
		ConsolePrinter.println("调用类和方法: " + fullQualifiedClassName + "#" + methodName);
		ConsolePrinter.println();
		ConsolePrinter.println("调用参数是:" + paramNames);
		ConsolePrinter.println();
		ConsolePrinter.println("-----------Action运行之后...end-----------\n");
		return true;
	}

	/**
	 * 调用目标方法之后, 但渲染视图之前. 可以对请求域中的属性或视图做出修改.
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
//		ConsolePrinter.println("[ActionDebugInterceptor] postHandle");
	}

	/**
	 * 渲染视图之后被调用. 释放资源
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
//		ConsolePrinter.println("[ActionDebugInterceptor] afterCompletion");
	}

}
