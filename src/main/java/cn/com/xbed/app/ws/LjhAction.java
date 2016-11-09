package cn.com.xbed.app.ws;

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

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.service.BreakfastService;
import cn.com.xbed.app.service.ChainMgntService;
import cn.com.xbed.app.service.OrderMgntService;
import cn.com.xbed.app.service.ljh.LjhService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

/**
 * 给丽家会调用的接口
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/ljh")
public class LjhAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LjhAction.class));
	@Resource
	private LjhService ljhService;
	@Resource
	private ChainMgntService chainMgntService;
	@Resource
	private BreakfastService breakfastService;
	@Resource
	private OrderMgntService orderMgntService;

	@ResponseBody
	@RequestMapping(value = "/sendsms", method = RequestMethod.POST)
	public Map<String, Object> sendSms(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("丽家会发短信:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String mobile = entity.getString("mobile");
			String content = entity.getString("content");
			AssertHelper.notNullEmptyBlank("JSON传参错误", mobile, content);
			if (mobile.length() != 11) {
				throw new RuntimeException("手机号码错误:" + mobile);
			}
			ljhService.sendSms(mobile, content);
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 查询今日的预计退房数 {date:"2015-12-11"}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/precheckout", method = RequestMethod.POST)
	public Map<String, Object> queryPreCheckout(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("丽家会查预离数:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String date = entity.getString("date");
			AssertHelper.notNullEmptyBlank("Json传参错误", date);
			Map<String, Object> result = orderMgntService.queryPreCheckout(DateUtil.parseDate(date));
			exceptionHandler.getLog().info("丽家会查预离数:" + result);
			RestCallHelper.fillSuccessParams(resultMap, result);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

}
