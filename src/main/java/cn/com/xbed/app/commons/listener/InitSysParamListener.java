package cn.com.xbed.app.commons.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.com.xbed.app.service.sysparam.SystemParamService;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

public class InitSysParamListener implements ServletContextListener {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(InitSysParamListener.class));

	// 因为Spring加载时机问题不能这么注入
	// @Resource
	private SystemParamService systemParamService;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		if (springContext != null) {
			/*
			 * 	和通过@Resource注入的对象是同一个对象！
			 *  listener-->cn.com.xbed.app.service.SystemParamService@35bf9f81
				action-->cn.com.xbed.app.service.SystemParamService@35bf9f81
			 */
			systemParamService = (SystemParamService) springContext.getBean(SystemParamService.class);
			if (systemParamService != null) {
				systemParamService.initAllSysParam();
				//System.out.println("listener-->" + systemParamService);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}
}
