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

import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.service.owner.OwnerService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

/**
 * 给业主APP调用的接口
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/owner")
public class OwnerAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OwnerAction.class));
	@Resource
	private OrderUtil orderUtil;
	@Resource
	private OwnerService ownerService;

	// {bookName:"下单人姓名",bookMobile:"下单人手机",begin:"2015-12-18",end:"2015-12-19",roomId:1,checkinName:"入住人姓名",checkinMobile:"入住人手机"}checkinName和checkinMobile可以不传,则默认是bookName和bookMobile
	@ResponseBody
	@RequestMapping(value = "/neworder", method = RequestMethod.POST)
	public Map<String, Object> addOwnerOrder(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			if (jsonStr == null) {
				throw new RuntimeException("请传参数,使用键值对方式,key是json_data=json格式的字符串");
			}
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			String bookName = entity.getString("bookName");
			String bookMobile = entity.getString("bookMobile");
			String begin = entity.getString("begin");
			String end = entity.getString("end");
			Integer roomId = entity.getInteger("roomId");
			AssertHelper.notNull(bookName, bookMobile, begin, end, roomId, "JSON传参错误");
			String checkinName = entity.getString("checkinName");
			String checkinMobile = entity.getString("checkinMobile");
			ownerService.addOwnerOrder(bookName, bookMobile, begin, end, roomId, checkinName, checkinMobile);
			RestCallHelper.fillSuccessParams(resultMap, null);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	//{mobile:"13042014179",content:"test"}
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
			ownerService.sendSms(mobile, content);
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
	 * {name:"test",mobile:"13042014179"}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/newowner", method = RequestMethod.POST)
	public Map<String, Object> newOwner(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("调用新增业主接口,入参:" + jsonStr);
			
			Map<String, Object> result = ownerService.newOwner(jsonStr);
			boolean isNewCreate = (boolean) result.get("isNewCreate");
			Map<String, Object> subResultMap = new HashMap<>();
			if (isNewCreate) {
				subResultMap.put("isNewCreate", 1);
			} else {
				subResultMap.put("isNewCreate", 0);
			}
			RestCallHelper.fillSuccessParams(resultMap, subResultMap);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
}
