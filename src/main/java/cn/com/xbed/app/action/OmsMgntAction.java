package cn.com.xbed.app.action;

import java.util.LinkedHashMap;
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

import cn.com.xbed.app.bean.XbOrderStop;
import cn.com.xbed.app.bean.vo.OtaChannelNewOrdVo;
import cn.com.xbed.app.bean.vo.PromoNewOrd;
import cn.com.xbed.app.service.LodgerService;
import cn.com.xbed.app.service.OmsMgntService;
import cn.com.xbed.app.service.OmsUserService;
import cn.com.xbed.app.service.OrderMgntService;
import cn.com.xbed.app.service.RoomMgntService;
import cn.com.xbed.app.service.stopmodule.vo.StopEntity;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;

@Controller
@RequestMapping("/app/ordmgnt")// 可以跟其他类的一样
public class OmsMgntAction {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OmsMgntAction.class));
	@Resource
	private OrderMgntService orderMgntService;

	@Resource
	private RoomMgntService roomMgntService;
	@Resource
	private OmsMgntService omsMgntService;
	
	@Resource
	private LodgerService lodgerService;
	@Resource
	private OmsUserService omsUserService;

	/**
	 * 查询活动的订单  {orderNo:"2005255255"}  tripNo非必传 orderNo非必传
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/querypromoorder", method = RequestMethod.POST)
	public Map<String, Object> queryPromoOrder(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req, true);
			String orderNo = null;
			if (StringUtils.isNotBlank(jsonStr) && !jsonStr.equals("null")) {
				JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
				orderNo = entity.getString("orderNo");
			}
			RestCallHelper.fillSuccessParams(resultMap, omsMgntService.queryPromoOrder(orderNo));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 新增活动订单
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addpromoorder", method = RequestMethod.POST)
	public Map<String, Object> addPromoOrder(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			PromoNewOrd promoNewOrd = JsonHelper.parseJSONStr2T(jsonStr, PromoNewOrd.class);
			RestCallHelper.fillSuccessParams(resultMap, omsMgntService.addPromoOrder(promoNewOrd));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 新增渠道订单(携程,美团...)
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addotachannelorder", method = RequestMethod.POST)
	public Map<String, Object> addOtaChannelOrder(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			OtaChannelNewOrdVo otaChannelNewOrdVo = JsonHelper.parseJSONStr2T(jsonStr, OtaChannelNewOrdVo.class);
			RestCallHelper.fillSuccessParams(resultMap, omsMgntService.addOtaChannelOrder(otaChannelNewOrdVo));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询携程的订单  {otaChannelNo:"48558588",orderNo:"2005255255"}  tripNo非必传 orderNo非必传
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryotachannelorder", method = RequestMethod.POST)
	public Map<String, Object> queryOtaChannelOrder(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req, true);
			String otaChannelNo = null;
			String orderNo = null;
			if (StringUtils.isNotBlank(jsonStr) && !jsonStr.equals("null")) {
				JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
				otaChannelNo = entity.getString("otaChannelNo");
				orderNo = entity.getString("orderNo");
			}
			RestCallHelper.fillSuccessParams(resultMap, omsMgntService.queryOtaChannelOrder(otaChannelNo, orderNo));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	
	/**
	 * 新增停用单       { roomId:1,stopBegin:"2015-12-22",stopEnd:"2015-12-24",contactName:"王尼玛",contactMobile:"1388888888",memo:"因为某某原因要要停用",userId:122,userAcct:"zhongsm"} 所有字段必传,memo实在不传则传空串即可. userId为oms管理员ID                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addstopsheet", method = RequestMethod.POST)
	public Map<String, Object> addStopSheet(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer roomId = entity.getInteger("roomId");
			String stopBegin = entity.getString("stopBegin");
			String stopEnd = entity.getString("stopEnd");
			String contactName = entity.getString("contactName");
			String contactMobile = entity.getString("contactMobile");
			String memo = entity.getString("memo");
			Integer userId = entity.getInteger("userId");
			String userAcct = entity.getString("userAcct");
			AssertHelper.notNull(roomId, stopBegin, stopEnd, contactName, contactMobile, memo, userId, userAcct, "JSON入参错误");
			RestCallHelper.fillSuccessParams(resultMap, omsMgntService.addStopSheet(new StopEntity(roomId, stopBegin, stopEnd, contactName, contactMobile, userId, userAcct, memo)));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 查询停用单  {thisPage:12,pageSize:1}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/querystopsheet", method = RequestMethod.POST)
	public Map<String, Object> queryStopSheet(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer thisPage = entity.getInteger("thisPage");
			Integer pageSize = entity.getInteger("pageSize");
			AssertHelper.notNull("JSON传参错误", thisPage, pageSize);
			RestCallHelper.fillSuccessParams(resultMap, omsMgntService.queryStopSheet(thisPage, pageSize));
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
	/**
	 * 取消停用单	 {stopId:123,userId:123,userAcct:"zhongsm"}
	 * @param req
	 * @param resp
	 * @return
	 */
	//{stopId:123,userId:123,userAcct:"zhongsm"}
	@ResponseBody
	@RequestMapping(value = "/cancelstopsheet", method = RequestMethod.POST)
	public Map<String, Object> cancelStopSheet(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer stopId = entity.getInteger("stopId");
			Integer userId = entity.getInteger("userId");
			String userAcct = entity.getString("userAcct");
			AssertHelper.notNull(stopId, userId, userAcct, "JSON传参错误");
			omsMgntService.cancelStopSheet(stopId, userId, userAcct);
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
	 * OMS查询开门密码  {checkinId:2323,}
	 * @param req
	 * @param resp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doorpwd", method = RequestMethod.POST)
	public Map<String, Object> queryDoorPwd(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		try {
			String jsonStr = HttpHelper.getDefaultJsonString(req);
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			Integer checkinId = entity.getInteger("checkinId");
			AssertHelper.notNull("JSON传参错误", checkinId);
			
			omsMgntService.queryDoorPwd(checkinId, resultMap);
		} catch (Exception e) {
			RestCallHelper.fillExceptionParams(resultMap, e);
			exceptionHandler.logActionException(e);
		} finally {
			HttpHelper.setRespAccessControl(resp);
		}
		return resultMap;
	}
	
}
