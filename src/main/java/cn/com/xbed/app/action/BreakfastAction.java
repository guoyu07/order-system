package cn.com.xbed.app.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.service.BreakfastService;
import cn.com.xbed.app.service.ChainMgntService;
import cn.com.xbed.app.service.ValidService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

@Controller
@RequestMapping("/app/bf")
public class BreakfastAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(BreakfastAction.class));
	@Resource
	private ChainMgntService chainMgntService;
	@Resource
	private BreakfastService breakfastService;

	
	
	@ResponseBody
	@RequestMapping(value = "/sendsms", method = RequestMethod.POST)
	public Map<String, Object> sendSms(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String mobile = entity.getString("mobile");
			String content = entity.getString("content");
			AssertHelper.notNullEmptyBlank("JSON传参错误", mobile, content);
			if (mobile.length() != 11) {
				throw new RuntimeException("手机号码错误:" + mobile);
			}
			breakfastService.sendSmsAndLogRecord(mobile, content);
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
	 * 获取门店
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/get_hotel", method = RequestMethod.POST)
	public Map<String, Object> getHotel(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			//String jsonStr = HttpHelper.getDefaultJsonString(req);
			//JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			List<XbChain> chainList = chainMgntService.queryAllChainInfo();
			List<Map<String, String>> mapList = new ArrayList<>();
			int chainSize = chainList.size();
			for (int i = 0; i < chainSize; i++) {
				XbChain chainInfo = chainList.get(i);
				Map<String, String> tmp = new HashMap<>();
				tmp.put("hotelId", chainInfo.getChainId()+"");
				tmp.put("hotelName", chainInfo.getName());
				tmp.put("hotelAddr", chainInfo.getAddress());
				tmp.put("help400", "4003901333");
				mapList.add(tmp);
			}
			RestCallHelper.fillSuccessParams(resultMap, mapList);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 获取用户信息
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/get_user", method = RequestMethod.POST)
	public Map<String, Object> getUser(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			Integer lodgerId = entity.getInteger("lodgerId");
			AssertHelper.notNull("JSON传参错误",checkinId , lodgerId);
			Map<String, String> retMap = breakfastService.queryCheckinInfo(checkinId, lodgerId);
			RestCallHelper.fillSuccessParams(resultMap, retMap);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
}

