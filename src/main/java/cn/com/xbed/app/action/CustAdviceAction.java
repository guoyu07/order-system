package cn.com.xbed.app.action;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.xbed.app.bean.XbCustAdvice;
import cn.com.xbed.app.service.CustAdviceService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

@Controller
@RequestMapping("/app/adv")
public class CustAdviceAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CustAdviceAction.class));
	@Resource
	private CustAdviceService custAdviceService;

	/**
	 * 增加客人建议的接口 【作废,前端没用到】
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Map<String, Object> addAdvice(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			XbCustAdvice entity = JsonHelper.parseJSONStr2T(jsonStr, XbCustAdvice.class);
			custAdviceService.addCustAdvice(entity);
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
