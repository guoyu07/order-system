package cn.com.xbed.app.ws;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.service.LodgerService;
import cn.com.xbed.app.service.xmonitor.XMonitorService;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

/**
 * 给XMonitor调用的接口
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/common")
public class XMonitorAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(XMonitorAction.class));
	@Resource
	private LodgerService lodgerService;
	@Resource
	private XMonitorService xMonitorService;

	/**
	 * {lodgerId:12324}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/qrylodger", method = RequestMethod.POST)
	public Map<String, Object> qryLodger(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("xmonitor调用我查询用户信息" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer lodgerId = entity.getInteger("lodgerId");
			AssertHelper.notNull(lodgerId, "JSON传参错误");

			List<XbLodger> list = lodgerService.queryLodgerInfo(lodgerId, null, null, null);
			XbLodger lodgerInfo = list.isEmpty() ? null : list.get(0);
			if (lodgerInfo != null) {
				lodgerInfo.setPassword2(null);
			}
			RestCallHelper.fillSuccessParams(resultMap, lodgerInfo);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}

	/**
	 * 查询下一任的入住人
	 * {roomId:123, date:"2015-12-17 11:16:33"}
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/qrynextlodger", method = RequestMethod.POST)
	public Map<String, Object> qryNextLodger(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("xmonitor调用我查询下一任" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			Date today = entity.getDate("date");
			AssertHelper.notNull(roomId, "JSON传参错误");
			RestCallHelper.fillSuccessParams(resultMap, xMonitorService.qryNextLodger(roomId, today));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询当前room_id住户 {roomId:123}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/qrynowlodger", method = RequestMethod.POST)
	public Map<String, Object> qryNowlodger(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("xmonitor调用我查询现任" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			AssertHelper.notNull(roomId, "JSON传参错误");
			RestCallHelper.fillSuccessParams(resultMap, xMonitorService.qryNowlodger(roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询订单信息 {roomId:123} 
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/qryroominfo", method = RequestMethod.POST)
	public Map<String, Object> qryRoomInfo(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			exceptionHandler.getLog().info("xmonitor查订单相关信息" + jsonStr);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			AssertHelper.notNull(roomId, "传参错误");
			RestCallHelper.fillSuccessParams(resultMap, xMonitorService.qryRoomInfo(roomId));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
}
