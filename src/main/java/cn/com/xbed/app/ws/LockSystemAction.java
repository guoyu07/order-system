package cn.com.xbed.app.ws;

import java.util.HashMap;
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

import cn.com.xbed.app.service.ValidService;
import cn.com.xbed.app.service.locksystem.LockSystemService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

/**
 * 给门锁系统调用的接口(KIRA)
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/locksystem")
public class LockSystemAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LockSystemAction.class));
	@Resource
	private LockSystemService lockSystemService;
	@Resource
	private ValidService validService;

	/**
	 * 入参 {mobile:"13042014179",content:"test"}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendsms", method = RequestMethod.POST)
	public Map<String, Object> sendSms(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("门锁系统发短信:" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String mobile = entity.getString("mobile");
			String content = entity.getString("content");
			AssertHelper.notNullEmptyBlank("JSON传参错误", mobile, content);
			if (mobile.length() != 11) {
				throw new RuntimeException("手机号码错误:" + mobile);
			}
			lockSystemService.sendSms(mobile, content);
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
	 * 发送验证短信，验证码 {mobile:"13431092223"}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendrandom", method = RequestMethod.POST)
	public Map<String, Object> sendRandom(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String mobile = entity.getString("mobile");
			AssertHelper.notNullEmptyBlank("传参错误", mobile);
			int valid = validService.sendRandomSms(mobile);
			Map<String, Object> subMap = new HashMap<>();
			subMap.put("validId", valid);
			RestCallHelper.fillSuccessParams(resultMap, subMap);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 验证随机码{validId:1323,validVal:"032323"}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/validrandom", method = RequestMethod.POST)
	public Map<String, Object> validRandom(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer validId = entity.getInteger("validId");
			String validVal = entity.getString("validVal");
			AssertHelper.notNull("传参错误", validId);
			AssertHelper.notNullEmptyBlank("传参错误", validVal);
			validService.validRandom(validId, validVal);
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
