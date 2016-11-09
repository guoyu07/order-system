package cn.com.xbed.app.service.roomstatemodule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;

@Service
@Transactional
public class SendDatagram2RoomStateSystemTool {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SendDatagram2RoomStateSystemTool.class));

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public String getNewOrderDatagramV3(int orderId) {
		try {
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
			AssertHelper.notNull(orderInfo, "查无订单,orderId:" + orderId);
			List<XbCheckin> list = daoUtil.checkinDao.findByOrderId(orderId);
			AssertHelper.notEmpty(list, "查无入住单,orderId:" + orderId);
			
			String orderNo = orderInfo.getOrderNo();
			Map<String, Object> reqParam = new LinkedHashMap<>();// 报文
			reqParam.put("xbedOrderNo", orderNo);
			
			List<Map<String, String>> stayInfoList = new ArrayList<>();
			int chainId = 0;
			/*
			 *关于价格问题，xb_order.total_price(实付价格,历史原因用了"total")记录的是实付价格，discount_price是优惠了多少钱，这两个字段相加是原价。
			 *
			 *xb_checkin.chkin_price记录的是某间房的总价(一天或多天)，注意这个价格是不受优惠券影响的，总是符合: a订单下有b和c两个入住单，那么永远成立b.chkin_price+c.chkin_price=a.total_price+a.discount_price
			 *
			 *如果是用了优惠券的一单多房，取xb_order.total_price(实付价格)，并且平均分在多间房间(除不尽的余数分到第一间房)
			 *
			 *记得，如果优惠后的实付为0元，还必须改成1元(否则去呼呼失败)
			 *
			 */
			Map<String, Double> priceMap = new LinkedHashMap<>();
			int size = list.size();
			int orderPrice = orderInfo.getTotalPrice();// 订单是实付价格
			int remainder = orderPrice % size;// 余数
			int evg = orderPrice / size;
			int evgCentUnit = (evg / 100)*100;// 分单位，舍弃了分
			
			int re = 0;// 每次结余分数
			for (int i = 0; i < size; i++) {
				
				re += (evg - evgCentUnit);
				
				int roomPrice = 0;
				if (i == size - 1) {
					roomPrice = evgCentUnit + remainder + re;
				} else {
					roomPrice = evgCentUnit;
				}
				String key = list.get(i).getCheckinId() + "";
				double roomPriceYuanUnit = Math.ceil((double) roomPrice / 100.00) == 0 ? 1 : Math.ceil((double) roomPrice / 100.00);
				priceMap.put(key, roomPriceYuanUnit);
			}
			
			
			
			for (XbCheckin checkinInfo : list) {
				XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
				AssertHelper.notNull(roomInfo, "查无订单,roomId:" + checkinInfo.getRoomId());

				int checkinId = checkinInfo.getCheckinId();
				chainId = roomInfo.getChainId();
				int roomId = roomInfo.getRoomId();
				
				Date checkinBeginTime = checkinInfo.getCheckinTime();
				Date checkinEndTime = checkinInfo.getCheckoutTime();
				int roomPriceCentUnit = checkinInfo.getChkinPrice();
				String custName = checkinInfo.getLodgerName();
				String custMobile = checkinInfo.getContactPhone();
				String contName = custName;
				String contMobile = custMobile;
				double roomPrice = priceMap.get(checkinId + "");
				String priceSendQhh = new Double(roomPrice).intValue() + "";// 发送给去呼的钱
				
				
				
				Map<String, String> reqParamSubMap = new LinkedHashMap<>();
				reqParamSubMap.put("roomId", roomId + "");
				reqParamSubMap.put("channelOrderNo", orderNo);
				reqParamSubMap.put("checkInTime", DateUtil.dateToString(checkinBeginTime, DateUtil.yrMonDayHrMinSec));
				reqParamSubMap.put("checkOutTime", DateUtil.dateToString(checkinEndTime, DateUtil.yrMonDayHrMinSec));
				reqParamSubMap.put("customerName", custName);
				reqParamSubMap.put("customerMobile", custMobile);
				reqParamSubMap.put("contactName", contName);
				reqParamSubMap.put("contactMobile", contMobile);
				reqParamSubMap.put("roomPrice",  priceSendQhh);
				reqParamSubMap.put("checkinId", checkinId + "");
				reqParamSubMap.put("createSource", this.getSource(orderInfo.getSource()));
				
				stayInfoList.add(reqParamSubMap);
				reqParam.put("stayInfo", stayInfoList);
			}
			reqParam.put("hotelId", chainId + "");// 理论上本地渠道的一单多房都是同一个门店，这里取一单多房最后一个房间的chain_id，理论上和其他房间无差别
			
			
			
			
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送房态接口报文,新订单:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public String getNewOrderDatagramV2(int checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			AssertHelper.notNull(roomInfo, "查无订单,roomId:" + checkinInfo.getRoomId());

			int chainId = roomInfo.getChainId();
			int roomId = roomInfo.getRoomId();
			String orderNo = orderInfo.getOrderNo();
			Date checkinBeginTime = checkinInfo.getCheckinTime();
			Date checkinEndTime = checkinInfo.getCheckoutTime();
			int roomPriceCentUnit = checkinInfo.getChkinPrice();
			String custName = checkinInfo.getLodgerName();
			String custMobile = checkinInfo.getContactPhone();
			String contName = custName;
			String contMobile = custMobile;
			int orderPrice = orderInfo.getTotalPrice();
			double roomPrice = Math.ceil((double)orderPrice / 100.00) == 0 ? 1 : Math.ceil((double)orderPrice / 100.00);
			String priceSendQhh = new Double(roomPrice).intValue() + "";// 发送给去呼的钱
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
			reqParamSubMap.put("roomPrice",  priceSendQhh);
			reqParamSubMap.put("checkinId", checkinId + "");
			reqParamSubMap.put("createSource", this.getSource(orderInfo.getSource()));
			List<Map<String, String>> stayInfoList = new ArrayList<>();
			stayInfoList.add(reqParamSubMap);
			reqParam.put("stayInfo", stayInfoList);
			reqParam.put("xbedOrderNo", orderNo);
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送房态接口报文,新订单:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	// 去呼呼创建订单【作废作废作废】
	public String getNewOrderDatagram(int orderId) {
		try {
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
			AssertHelper.notNull(orderInfo, "查无订单,orderId:" + orderId);
			List<XbCheckin> list = daoUtil.checkinDao.findByOrderId(orderId);
			AssertHelper.notEmpty(list, "查无入住单,orderId:" + orderId);
			XbCheckin checkinInfo = list.get(0);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			AssertHelper.notNull(roomInfo, "查无订单,roomId:" + checkinInfo.getRoomId());

			int checkinId = checkinInfo.getCheckinId();
			int chainId = roomInfo.getChainId();
			int roomId = roomInfo.getRoomId();
			String orderNo = orderInfo.getOrderNo();
			Date checkinBeginTime = checkinInfo.getCheckinTime();
			Date checkinEndTime = checkinInfo.getCheckoutTime();
			int roomPriceCentUnit = checkinInfo.getChkinPrice();
			String custName = checkinInfo.getLodgerName();
			String custMobile = checkinInfo.getContactPhone();
			String contName = custName;
			String contMobile = custMobile;
			int orderPrice = orderInfo.getTotalPrice();
			double roomPrice = Math.ceil((double)orderPrice / 100.00) == 0 ? 1 : Math.ceil((double)orderPrice / 100.00);
			String priceSendQhh = new Double(roomPrice).intValue() + "";// 发送给去呼的钱
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
			reqParamSubMap.put("roomPrice",  priceSendQhh);
			reqParamSubMap.put("checkinId", checkinId + "");
			reqParamSubMap.put("createSource", this.getSource(orderInfo.getSource()));
			List<Map<String, String>> stayInfoList = new ArrayList<>();
			stayInfoList.add(reqParamSubMap);
			reqParam.put("stayInfo", stayInfoList);
			reqParam.put("xbedOrderNo", orderNo);
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送房态接口报文,新订单:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public String getNewCheckinDatagram(int checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			AssertHelper.notNull(roomInfo, "查无订单,roomId:" + checkinInfo.getRoomId());

			int chainId = roomInfo.getChainId();
			int roomId = roomInfo.getRoomId();
			String orderNo = orderInfo.getOrderNo();
			Date checkinBeginTime = checkinInfo.getCheckinTime();
			Date checkinEndTime = checkinInfo.getCheckoutTime();
			int roomPriceCentUnit = checkinInfo.getChkinPrice();
			String custName = checkinInfo.getLodgerName();
			String custMobile = checkinInfo.getContactPhone();
			String contName = custName;
			String contMobile = custMobile;

			int orderPrice = orderInfo.getTotalPrice();
			double roomPrice = Math.ceil((double)orderPrice / 100.00) == 0 ? 1 : Math.ceil((double)orderPrice / 100.00);
			String priceSendQhh = new Double(roomPrice).intValue() + "";// 发送给去呼的钱
			
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
			reqParamSubMap.put("roomPrice", priceSendQhh);
			reqParamSubMap.put("checkinId", checkinId + "");
			reqParamSubMap.put("createSource", this.getSource(orderInfo.getSource()));
			List<Map<String, String>> stayInfoList = new ArrayList<>();
			stayInfoList.add(reqParamSubMap);
			reqParam.put("stayInfo", stayInfoList);
			reqParam.put("xbedOrderNo", orderNo);
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送房态接口报文,新订单:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	private String getSource(int source) {
		return source == AppConstants.Order_source.QUNAR_1 ? "qhh" : "self";
	}

	// 去呼呼办理入住,办理失败抛错
	public String getCheckinDatagram(int checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			String otaOrderNo = orderInfo.getOtaOrderNo();
			String otaStayId = checkinInfo.getOtaStayId();
			String orderNo = orderInfo.getOrderNo();
			int roomId = roomInfo.getRoomId();
			int chainId = roomInfo.getChainId();
			exceptionHandler.getLog().info("去呼呼同步接口.入住:" + otaOrderNo + "," + otaStayId + "," + roomId + "," + chainId);

			Map<String, String> reqParam = new HashMap<>();
			reqParam.put("hotelId", chainId + "");// 我们在去呼呼
			reqParam.put("roomId", roomId + "");// 我们在去呼呼后台填写的房号(roomNo),即roomName
			reqParam.put("orderNo", otaOrderNo + "");// 去呼呼的order_no
			reqParam.put("stayId", otaStayId + "");// 去呼呼给的stayId
			reqParam.put("checkinId", checkinId + "");
			reqParam.put("createSource", this.getSource(orderInfo.getSource()));
			reqParam.put("xbedOrderNo", orderNo);

			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送房态接口报文,入住:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 去呼呼办理退房
	public String getCheckoutDatagram(int checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			String otaOrderNo = orderInfo.getOtaOrderNo();
			String otaStayId = checkinInfo.getOtaStayId();
			String orderNo = orderInfo.getOrderNo();
			int roomId = roomInfo.getRoomId();
			int chainId = roomInfo.getChainId();

//			if (otaOrderNo == null || otaStayId == null) {
//				throw new RuntimeException("otaOrderNo或otaStayId为null,otaOrderNo:" + otaOrderNo + ",otaStayId:" + otaStayId);
//			}

			Map<String, String> reqParam = new HashMap<>();
			reqParam.put("hotelId", chainId + "");// 我们在去呼呼
			reqParam.put("roomId", roomId + "");// 我们在去呼呼后台填写的房号(roomNo),即roomName
			reqParam.put("orderNo", otaOrderNo + "");// 去呼呼的order_no
			reqParam.put("stayId", otaStayId + "");// 去呼呼给的stayId
			reqParam.put("checkinId", checkinId + "");
			reqParam.put("createSource", this.getSource(orderInfo.getSource()));
			reqParam.put("xbedOrderNo", orderNo);
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送房态接口报文,退房:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}

	}// 去呼呼办理退房

	public String getCancelDatagram(int checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			String otaOrderNo = orderInfo.getOtaOrderNo();
			String otaStayId = checkinInfo.getOtaStayId();
			String orderNo = orderInfo.getOrderNo();
			int roomId = roomInfo.getRoomId();
			int chainId = roomInfo.getChainId();


			Map<String, String> reqParam = new HashMap<>();
			reqParam.put("hotelId", chainId + "");// 我们在去呼呼
			reqParam.put("roomId", roomId + "");// 我们在去呼呼后台填写的房号(roomNo),即roomName
			reqParam.put("orderNo", otaOrderNo + "");// 去呼呼的order_no
			reqParam.put("stayId", otaStayId + "");// 去呼呼给的stayId
			reqParam.put("checkinId", checkinId + "");
			reqParam.put("createSource", this.getSource(orderInfo.getSource()));
			reqParam.put("xbedOrderNo", orderNo);
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送房态接口报文,取消:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
