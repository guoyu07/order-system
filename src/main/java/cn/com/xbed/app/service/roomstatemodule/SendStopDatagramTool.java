package cn.com.xbed.app.service.roomstatemodule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbOrderStop;
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
public class SendStopDatagramTool {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SendStopDatagramTool.class));

	// 新建停用单
	public String getNewStopDatagram(int stopId) {
		try {
			XbOrderStop stop = daoUtil.orderStopDao.findByPk(stopId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(stop.getRoomId());
			
			int chainId = roomInfo.getChainId();
			int roomId = roomInfo.getRoomId();
			String stopNo = stop.getStopNo();
			Date checkinBeginTime = stop.getStopBegin();
			Date checkinEndTime = stop.getStopEnd();
			int totalPriceCentUnit = stop.getPrice();
			String custName = stop.getContactName();
			String custMobile = stop.getContactMobile();
			String contName = custName;
			String contMobile = custMobile;

			Map<String, Object> reqParam = new HashMap<>();
			reqParam.put("hotelId", chainId + "");
			Map<String, String> reqParamSubMap = new HashMap<>();
			reqParamSubMap.put("roomId", roomId + "");
			reqParamSubMap.put("channelOrderNo", stopNo);
			reqParamSubMap.put("checkInTime", DateUtil.dateToString(checkinBeginTime, DateUtil.yrMonDayHrMinSec));
			reqParamSubMap.put("checkOutTime", DateUtil.dateToString(checkinEndTime, DateUtil.yrMonDayHrMinSec));
			reqParamSubMap.put("customerName", custName);
			reqParamSubMap.put("customerMobile", custMobile);
			reqParamSubMap.put("contactName", contName);
			reqParamSubMap.put("contactMobile", contMobile);
			reqParamSubMap.put("roomPrice", (totalPriceCentUnit / 100) + "");
			reqParamSubMap.put("createSource", this.getSource());
			reqParamSubMap.put("checkinId", stopId + "");
			List<Map<String, String>> stayInfoList = new ArrayList<>();
			stayInfoList.add(reqParamSubMap);
			reqParam.put("stayInfo", stayInfoList);
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送停用单,新订单:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	private String getSource() {
		return "self";
	}

	// 停用单开始
	public String getStopBeginDatagram(int stopId) {
		try {
			XbOrderStop stop = daoUtil.orderStopDao.findByPk(stopId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(stop.getRoomId());
			
			
			String otaOrderNo = stop.getOtaOrderNo();
			String otaStayId = stop.getOtaStayId();
			int roomId = roomInfo.getRoomId();
			int chainId = roomInfo.getChainId();
			exceptionHandler.getLog().info("去呼呼同步接口.入住:" + otaOrderNo + "," + otaStayId + "," + roomId + "," + chainId);

			Map<String, String> reqParam = new HashMap<>();
			reqParam.put("hotelId", chainId + "");// 我们在去呼呼
			reqParam.put("roomId", roomId + "");// 我们在去呼呼后台填写的房号(roomNo),即roomName
			reqParam.put("orderNo", otaOrderNo + "");// 去呼呼的order_no
			reqParam.put("stayId", otaStayId + "");// 去呼呼给的stayId
			reqParam.put("createSource", this.getSource());
			reqParam.put("checkinId", stopId + "");
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送停用单,入住:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 停用单结束
	public String getStopEndDatagram(int stopId) {
		try {
			XbOrderStop stop = daoUtil.orderStopDao.findByPk(stopId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(stop.getRoomId());
			
			String otaOrderNo = stop.getOtaOrderNo();
			String otaStayId = stop.getOtaStayId();
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
			reqParam.put("createSource", this.getSource());
			reqParam.put("checkinId", stopId + "");
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送停用单,退房:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}

	}

	// 停用单取消
	public String getStopCancelDatagram(int stopId) {
		try {
			XbOrderStop stop = daoUtil.orderStopDao.findByPk(stopId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(stop.getRoomId());
			
			String otaOrderNo = stop.getOtaOrderNo();
			String otaStayId = stop.getOtaStayId();
			int roomId = roomInfo.getRoomId();
			int chainId = roomInfo.getChainId();


			Map<String, String> reqParam = new HashMap<>();
			reqParam.put("hotelId", chainId + "");// 我们在去呼呼
			reqParam.put("roomId", roomId + "");// 我们在去呼呼后台填写的房号(roomNo),即roomName
			reqParam.put("orderNo", otaOrderNo + "");// 去呼呼的order_no
			reqParam.put("stayId", otaStayId + "");// 去呼呼给的stayId
			reqParam.put("createSource", this.getSource());
			reqParam.put("checkinId", stopId + "");
			String reqJsonStr = JsonHelper.toJSONString(reqParam);
			exceptionHandler.getLog().warn("发送停用单,取消:" + reqJsonStr);
			return reqJsonStr;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
