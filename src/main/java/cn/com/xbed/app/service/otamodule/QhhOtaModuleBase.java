package cn.com.xbed.app.service.otamodule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.util.RandomUtil;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.otamodule.vo.OtaNewOrderOut;

@Service
@Transactional
public class QhhOtaModuleBase implements IOtaModuleBase {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(QhhOtaModuleBase.class));

	// 去呼呼创建订单
	@Override
	public OtaNewOrderOut otaNewOrder(int orderId) {
		/*
		 * 入参: { "hotelId":"1", "stayInfo":"[ { "roomId":"1",
		 * "channelOrderNo":"23", "checkInTime":"20150922180000",
		 * "checkOutTime":"20150928240000", "customerName":"Pj",
		 * "customerMobile":"139261724343", "contactName":"Pj",
		 * "contactMobile":"134993131", "roomPrice":"100" }]" }
		 * 
		 * 出参: {"status":0,"errMsg":"调用成功","ver":"1.0","data":[{"orderNo":
		 * "17055526892014","stayId":"17125569","roomId":"3"}]}
		 */
		
		XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
		AssertHelper.notNull(orderInfo, "查无订单,orderId:" + orderId);
		List<XbCheckin> list = daoUtil.checkinDao.findByOrderId(orderId);
		AssertHelper.notEmpty(list, "查无入住单,orderId:" + orderId);
		XbCheckin checkinInfo = list.get(0);
		XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());

		
		int checkinId = checkinInfo.getCheckinId();
		int chainId = roomInfo.getChainId();
		int roomId = roomInfo.getRoomId();
		String orderNo = orderInfo.getOrderNo();
		Date checkinBeginTime = checkinInfo.getCheckinTime();
		Date checkinEndTime = checkinInfo.getCheckoutTime();
		int totalPriceCentUnit = orderInfo.getTotalPrice();
		String custName = checkinInfo.getLodgerName();
		String custMobile = checkinInfo.getContactPhone();
		String contName = custName;
		String contMobile = custMobile;
		exceptionHandler.getLog().info("去呼呼同步接口.新建:" + chainId + "," + roomId + "," + orderNo + "," + checkinBeginTime + "," + checkinEndTime + ","
				+ totalPriceCentUnit + "," + custName + "," + custMobile + "," + contName + "," + contMobile);
		
		try {
			String sw = daoUtil.sysParamDao.getValueByParamKeyNoThrow("switch.sync_method_qhh_new_order");
			if (sw == null || sw.equalsIgnoreCase("true")) {
				Map<String, Object> reqParam = new HashMap<>();
				reqParam.put("hotelId", chainId + "");
				Map<String, String> reqParamSubMap = new HashMap<>();
				reqParamSubMap.put("roomId", roomId + "");
				reqParamSubMap.put("channelOrderNo", orderNo);
				reqParamSubMap.put("checkInTime", DateUtil.dateToString(checkinBeginTime, DateUtil.yrMonDayHrMinSec));
				reqParamSubMap.put("checkOutTime", DateUtil.dateToString(checkinEndTime, DateUtil.yrMonDayHrMinSec));
				reqParamSubMap.put("customerName", custName);
				reqParamSubMap.put("customerMobile", custMobile);
				reqParamSubMap.put("contactName", contName);
				reqParamSubMap.put("contactMobile", contMobile);
				reqParamSubMap.put("roomPrice", (totalPriceCentUnit / 100) + "");
				List<Map<String, String>> stayInfoList = new ArrayList<>();
				stayInfoList.add(reqParamSubMap);
				reqParam.put("stayInfo", JsonHelper.toJSONString(stayInfoList));

				String reqJsonStr = JsonHelper.toJSONString(reqParam);
				String url = daoUtil.sysParamDao.getValueByParamKey("url.sync_method_qhh_new_order");
				exceptionHandler.getLog().warn("调用实时创建订单接口,入参:" + reqJsonStr);
				String reqOut = HttpHelper.requestByPostWithJsonParam(url, reqJsonStr);
				exceptionHandler.getLog().warn("调用实时创建订单接口,出参:" + reqOut);
				if (reqOut != null && reqOut.length() > 0) {
					JSONObject obj = JsonHelper.parseJSONStr2JSONObject(reqOut);
					String status = obj.getString("status");
					if (!status.equals("0")) {
						throw new RuntimeException("调用去呼呼实时创建订单失败" + reqOut);
					} else {
						JSONArray data = obj.getJSONArray("data");
						JSONObject dataObj = data.getJSONObject(0);
						return new OtaNewOrderOut(dataObj.getString("orderNo"), dataObj.getString("stayId"),
								Integer.parseInt(dataObj.getString("roomId")), checkinId);
					}
				} else {
					throw new RuntimeException("调用去呼呼实时创建订单失败" + reqOut);
				}
			} else {
				String otaOrderNo = "99" + RandomUtil.getRandomStr(12);
				String otaStayId = "99" + RandomUtil.getRandomStr(6);
				return new OtaNewOrderOut(otaOrderNo, otaStayId, roomId, checkinId);
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 去呼呼办理入住,办理失败抛错
	@Override
	public String otaCheckin(int checkinId) {

		/*
		 * 入参: { "hotelId":"1", "roomId":"3401", "orderNo":"318830",
		 * "stayId":"13380" }
		 * 
		 * 出参: { "status": 0, "errMsg": "办理入住成功! ", "ver": "1.0", "data": {
		 * "hzpwd": "123456 ",//管家密码，有锁房间时返回 "cuspwd": "678904 "//客人密码，有锁房间时返回 }
		 * }
		 * 
		 */
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			String otaOrderNo = orderInfo.getOtaOrderNo();
			String otaStayId = checkinInfo.getOtaStayId();
			int roomId = roomInfo.getRoomId();
			int chainId = roomInfo.getChainId();
			exceptionHandler.getLog().info("去呼呼同步接口.入住:" + otaOrderNo + "," + otaStayId + "," + roomId + "," + chainId);

			String lockPassword = "";
			String sw = daoUtil.sysParamDao.getValueByParamKeyNoThrow("switch.query_gatelockpwd_from_qunar");
			exceptionHandler.getLog().info("去呼呼同步接口.入住.开关:" + sw);

			if (sw == null || sw.equalsIgnoreCase("true")) {// 真密码
				if (otaOrderNo == null || otaStayId == null) {
					throw new RuntimeException("otaOrderNo或otaStayId为null,otaOrderNo:" + otaOrderNo + ",otaStayId:" + otaStayId);
				}

				Map<String, String> reqParam = new HashMap<>();
				reqParam.put("hotelId", chainId + "");// 我们在去呼呼
				reqParam.put("roomId", roomId + "");// 我们在去呼呼后台填写的房号(roomNo),即roomName
				reqParam.put("orderNo", otaOrderNo + "");// 去呼呼的order_no
				reqParam.put("stayId", otaStayId + "");// 去呼呼给的stayId
				String reqJsonStr = JsonHelper.toJSONString(reqParam);
				String url = daoUtil.sysParamDao.getValueByParamKey("url.query_gatelockpwd_from_qunar");
				String reqOut = HttpHelper.requestByPostWithJsonParam(url, reqJsonStr);
				exceptionHandler.getLog().warn("调用去呼呼入住接口获得开门密码,入参:" + reqJsonStr);
				exceptionHandler.getLog().warn("调用去呼呼入住接口获得开门密码,出参:" + reqOut);
				if (reqOut != null && reqOut.length() > 0) {
					JSONObject obj = JsonHelper.parseJSONStr2JSONObject(reqOut);
					String status = obj.getString("status");
					if (!status.equals("0")) {
						throw new RuntimeException("调用去呼呼门锁失败" + reqOut);
					}
					lockPassword = (String) obj.getJSONObject("data").get("cuspwd");
				} else {
					throw new RuntimeException("调用去呼呼门锁失败" + reqOut);
				}
				// if (lockPassword.length() == 0) {
				// throw new RuntimeException("去呼呼开门密码非法lockPassword:" +
				// lockPassword);
				// }
			} else {
				// 假密码
				lockPassword = "fake" + RandomUtil.getRandomStr(6);
			}
			exceptionHandler.getLog().info("去呼呼同步接口.入住.最终获得的开门密码:" + lockPassword);
			return lockPassword;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 去呼呼办理退房
	@Override
	public void otaCheckout(int checkinId) {
		/*
		 * 入参: { "hotelId":"1", "roomId":"3401", "orderNo":"318830",
		 * "stayId":"13380" } 出参: { "status": 0, "errMsg": "办理离店成功! ", "ver":
		 * "1.0", "data": "" }
		 */
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			String otaOrderNo = orderInfo.getOtaOrderNo();
			String otaStayId = checkinInfo.getOtaStayId();
			int roomId = roomInfo.getRoomId();
			int chainId = roomInfo.getChainId();
			exceptionHandler.getLog().info("去呼呼同步接口.退房:" + otaOrderNo + "," + otaStayId + "," + roomId + "," + chainId);

			String sw = daoUtil.sysParamDao.getValueByParamKeyNoThrow("switch.sync_method_qhh_checkout");
			exceptionHandler.getLog().info("去呼呼同步接口.退房.开关:" + sw);
			if (sw == null || sw.equalsIgnoreCase("true")) {
				if (otaOrderNo == null || otaStayId == null) {
					throw new RuntimeException("otaOrderNo或otaStayId为null,otaOrderNo:" + otaOrderNo + ",otaStayId:" + otaStayId);
				}

				Map<String, String> reqParam = new HashMap<>();
				reqParam.put("hotelId", chainId + "");// 我们在去呼呼
				reqParam.put("roomId", roomId + "");// 我们在去呼呼后台填写的房号(roomNo),即roomName
				reqParam.put("orderNo", otaOrderNo + "");// 去呼呼的order_no
				reqParam.put("stayId", otaStayId + "");// 去呼呼给的stayId

				String reqJsonStr = JsonHelper.toJSONString(reqParam);
				String url = daoUtil.sysParamDao.getValueByParamKey("url.sync_method_qhh_checkout");
				String reqOut = HttpHelper.requestByPostWithJsonParam(url, reqJsonStr);
				exceptionHandler.getLog().warn("调用实时退房接口,入参:" + reqJsonStr);
				exceptionHandler.getLog().warn("调用实时退房接口,出参:" + reqOut);
				if (reqOut != null && reqOut.length() > 0) {
					JSONObject obj = JsonHelper.parseJSONStr2JSONObject(reqOut);
					String status = obj.getString("status");
					if (!status.equals("0")) {
						throw new RuntimeException("调用去呼呼退房失败" + reqOut);
					}
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}

	}// 去呼呼办理退房

	@Override
	public void otaCancel(int orderId) {
		/*
		 * 入参: { "hotelId":"1", "roomId":"3401", "orderNo":"318830",
		 * "stayId":"13380" } 出参: { "status": 0, "errMsg": "办理离店成功! ", "ver":
		 * "1.0", "data": "" }
		 */
		try {
			List<XbCheckin> checkinInfoList = daoUtil.checkinDao.findByOrderId(orderId);
			AssertHelper.notEmpty(checkinInfoList, "入住单列表缺失,orderId:" + orderId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
			AssertHelper.notNull(orderInfo, "订单缺失,orderId:" + orderId);
			exceptionHandler.getLog().info("取消的订单包含入住单数:" + checkinInfoList.size());
			for (XbCheckin xbCheckin : checkinInfoList) {
				XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(xbCheckin.getRoomId());
				String otaOrderNo = orderInfo.getOtaOrderNo();
				String otaStayId = xbCheckin.getOtaStayId();
				int roomId = roomInfo.getRoomId();
				int chainId = roomInfo.getChainId();
				exceptionHandler.getLog().info("去呼呼同步接口.取消订单:" + otaOrderNo + "," + otaStayId + "," + roomId + "," + chainId);

				String sw = daoUtil.sysParamDao.getValueByParamKeyNoThrow("switch.sync_method_qhh_cancel");
				exceptionHandler.getLog().info("去呼呼同步接口.取消订单.开关:" + sw);
				if (sw == null || sw.equalsIgnoreCase("true")) {
					if (otaOrderNo == null || otaStayId == null) {
						throw new RuntimeException("otaOrderNo或otaStayId为null,otaOrderNo:" + otaOrderNo + ",otaStayId:" + otaStayId);
					}

					Map<String, String> reqParam = new HashMap<>();
					reqParam.put("hotelId", chainId + "");// 我们在去呼呼
					reqParam.put("roomId", roomId + "");// 我们在去呼呼后台填写的房号(roomNo),即roomName
					reqParam.put("orderNo", otaOrderNo + "");// 去呼呼的order_no
					reqParam.put("stayId", otaStayId + "");// 去呼呼给的stayId

					String reqJsonStr = JsonHelper.toJSONString(reqParam);
					String url = daoUtil.sysParamDao.getValueByParamKey("url.sync_method_qhh_cancel");
					String reqOut = HttpHelper.requestByPostWithJsonParam(url, reqJsonStr);
					exceptionHandler.getLog().warn("调用实时取消订单接口,入参:" + reqJsonStr);
					exceptionHandler.getLog().warn("调用实时取消订单接口,出参:" + reqOut);
					if (reqOut != null && reqOut.length() > 0) {
						JSONObject obj = JsonHelper.parseJSONStr2JSONObject(reqOut);
						String status = obj.getString("status");
						if (!status.equals("0")) {
							throw new RuntimeException("调用去呼呼取消订单失败" + reqOut);
						}
					} else {
						throw new RuntimeException("调用去呼呼取消订单失败" + reqOut);
					}
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
