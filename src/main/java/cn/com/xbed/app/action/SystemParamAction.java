package cn.com.xbed.app.action;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.xbed.app.service.sysparam.SystemParamService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;

@Controller
@RequestMapping("/app/sysparam")
public class SystemParamAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SystemParamAction.class));
	@Resource
	private SystemParamService systemParamService;

	/**
	 * 重新刷新系统参数sv_sys_param
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/refresh"/*, method = RequestMethod.POST*/)
	public Map<String, Object> refreshSysParam(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			systemParamService.initAllSysParam();
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

}
