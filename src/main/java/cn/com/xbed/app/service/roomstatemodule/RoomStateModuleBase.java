package cn.com.xbed.app.service.roomstatemodule;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.vo.CheckinInputVo;
import cn.com.xbed.app.bean.vo.CheckinerUnit;
import cn.com.xbed.app.bean.vo.CheckinerVo;
import cn.com.xbed.app.bean.vo.WarningEntity;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.enu.OperCodeCheckin;
import cn.com.xbed.app.commons.enu.OperCodeOrder;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.CommonDbLogService;
import cn.com.xbed.app.service.OrderMgntCommon;
import cn.com.xbed.app.service.OrderMgntService;
import cn.com.xbed.app.service.WarningSystemTool;

@Service
@Transactional
public class RoomStateModuleBase {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private OrderMgntService orderMgntService;
	@Resource
	private WarningSystemTool warningSystemTool;
	@Resource
	private CommonDbLogService commonDbLogService;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	@Resource
	private SendDatagram2RoomStateSystemTool sendDatagram2RoomStateSystemTool;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomStateModuleBase.class));

	public void roomStateNewOrder(int orderId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】新增订单,orderId:" + orderId);
			List<XbCheckin> list = daoUtil.checkinDao.findByOrderId(orderId);
			AssertHelper.notEmpty(list, "入住单列表为空,orderId:" + orderId);
			
			//临时代码..
			exceptionHandler.getLog().info("list.size() = " + list.size());
			exceptionHandler.getLog().info(JSONObject.toJSON(list));
//			for (XbCheckin xbCheckin : list) {
//				String datagram = sendDatagram2RoomStateSystemTool.getNewOrderDatagramV2(xbCheckin.getCheckinId());
//				calendarCommon.operOrder(xbCheckin.getRoomId(), xbCheckin.getCheckinTime(), xbCheckin.getCheckoutTime(), OperCodeOrder.NEW_ORDER, datagram);
//			}
			String datagram = sendDatagram2RoomStateSystemTool.getNewOrderDatagramV3(orderId);
			String roomId = "";
			for (int i = 0; i < list.size(); i++) {
				XbCheckin xbCheckin = list.get(i);
				if (i == list.size() - 1) {
					roomId += xbCheckin.getRoomId();
				} else {
					roomId += xbCheckin.getRoomId() + ",";
				}
			}
			calendarCommon.operOrder(roomId, list.get(0).getCheckinTime(), list.get(0).getCheckoutTime(), OperCodeOrder.NEW_ORDER, datagram);
			
			exceptionHandler.getLog().info("$$end【房态接口】新增订单,orderId:" + orderId);
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException("【房态接口】新增订单异常",e);
		}
	}
	
	
	/**
	 * 
	 * @param checkinId
	 */
	public void roomStateNewCheckin(int checkinId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】新增入住单,checkinId:" + checkinId);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			
			String datagram = sendDatagram2RoomStateSystemTool.getNewCheckinDatagram(checkinId);
			
			calendarCommon.operOrder(checkinInfo.getRoomId()+"", checkinInfo.getCheckinTime(), checkinInfo.getCheckoutTime(), OperCodeOrder.NEW_ORDER, datagram);
			exceptionHandler.getLog().info("$$end【房态接口】新增入住单,checkinId:" + checkinId);
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException("【房态接口】入住单异常", e);
		}
	}
	
	public JSONObject roomStateCheckin(int checkinId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】入住订单,checkinId:" + checkinId);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			String datagram = sendDatagram2RoomStateSystemTool.getCheckinDatagram(checkinId);

			JSONObject result = calendarCommon.operCheckin(checkinInfo.getRoomId(), checkinInfo.getCheckinTime(), checkinInfo.getCheckoutTime(), OperCodeCheckin.CHECKIN, datagram);
			exceptionHandler.getLog().info("$$end【房态接口】入住订单,checkinId:" + checkinId);
			return result;
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException("【房态接口】入住订单异常",e);
		}
	}

	public JSONObject roomStateCheckout(int checkinId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】离店订单,checkinId:" + checkinId);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			String datagram = sendDatagram2RoomStateSystemTool.getCheckoutDatagram(checkinId);

			JSONObject result = calendarCommon.operCheckin(checkinInfo.getRoomId(), checkinInfo.getCheckinTime(), checkinInfo.getCheckoutTime(), OperCodeCheckin.CHECKOUT, datagram);
			exceptionHandler.getLog().info("$$end【房态接口】离店订单,checkinId:" + checkinId);
			return result;
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException("【房态接口】离店订单异常",e);
		}
	}
	
	/**
	 * 续住退房
	 * @param checkinId
	 * @return
	 */
	public JSONObject roomStateOverstayCheckout(int checkinId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】续住退房,checkinId:" + checkinId);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			String datagram = sendDatagram2RoomStateSystemTool.getCheckoutDatagram(checkinId);

			JSONObject result = calendarCommon.operCheckin(checkinInfo.getRoomId(), checkinInfo.getCheckinTime(), checkinInfo.getCheckoutTime(), OperCodeCheckin.OVERSTAY_CHECKOUT, datagram);
			exceptionHandler.getLog().info("$$end【房态接口】续住退房,checkinId:" + checkinId);
			return result;
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException("【房态接口】续住退房异常",e);
		}
	}

	public void roomStateCancel(int orderId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】取消订单,orderId:" + orderId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
			AssertHelper.notNull(orderInfo, "查无订单,orderId:" + orderId);
			
			List<XbCheckin> list = daoUtil.checkinDao.findByOrderId(orderId);
			AssertHelper.notEmpty(list, "入住单列表为空,orderId:" + orderId);
			
			int orderStat = orderInfo.getStat();
			for (XbCheckin xbCheckin : list) {
				if (xbCheckin.getChgRoomFlag() != AppConstants.Checkin_chgRoomFlag.ORI_ROOM_1) {
					String datagram = sendDatagram2RoomStateSystemTool.getCancelDatagram(xbCheckin.getCheckinId());
					if (orderStat == AppConstants.Order_stat.NEW_0) {
						calendarCommon.operOrder(xbCheckin.getRoomId()+"", xbCheckin.getCheckinTime(), xbCheckin.getCheckoutTime(), OperCodeOrder.CANCEL_ORDER_UN_PAID, datagram);
					} else {
						calendarCommon.operOrder(xbCheckin.getRoomId()+"", xbCheckin.getCheckinTime(), xbCheckin.getCheckoutTime(), OperCodeOrder.CANCEL_ORDER_NOT_CHECKIN, datagram);
					}
				}
			}
			exceptionHandler.getLog().info("$$end【房态接口】取消订单,orderId:" + orderId);
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException("【房态接口】取消订单异常",e);
		}
	}
	
	
	public void roomStateCancelOneRoom(int checkinId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】取消入住单,checkinId:" + checkinId);
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			int orderStat = orderInfo.getStat();
			String datagram = sendDatagram2RoomStateSystemTool.getCancelDatagram(checkinInfo.getCheckinId());
			if (orderStat == AppConstants.Order_stat.NEW_0) {
				calendarCommon.operOrder(checkinInfo.getRoomId()+"", checkinInfo.getCheckinTime(), checkinInfo.getCheckoutTime(), OperCodeOrder.CANCEL_ORDER_UN_PAID, datagram);
			} else {
				calendarCommon.operOrder(checkinInfo.getRoomId()+"", checkinInfo.getCheckinTime(), checkinInfo.getCheckoutTime(), OperCodeOrder.CANCEL_ORDER_NOT_CHECKIN, datagram);
			}
			exceptionHandler.getLog().info("$$end【房态接口】取消订单,checkinId:" + checkinId);
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException("【房态接口】取消入住单异常",e);
		}
	}
	
	
	public void handle(String str) {
		exceptionHandler.getLog().info("MQ监听消息,进入了handle方法:" + str);
		boolean flag = true;
		int oriCheckinId = -1;
		Integer newCheckinId = -1;
		String errorMsg = "";
		try {
			JSONObject jsonObj = JsonHelper.parseJSONStr2JSONObject(str);
			JSONObject parameter = jsonObj.getJSONObject("parameters");
			JSONObject checkinInfo = parameter.getJSONObject("checkinInfo");
			oriCheckinId = checkinInfo.getIntValue("checkinId");
			exceptionHandler.getLog().info(String.format("自动办理入住,oriCheckinId:%s", oriCheckinId));
			newCheckinId = orderMgntCommon.queryOverstayCheckinId(oriCheckinId);
			exceptionHandler.getLog().info(String.format("自动办理入住,newCheckinId:%s", newCheckinId));
			if (newCheckinId != null) {
				XbCheckin newCheckinInfo = daoUtil.checkinDao.findByPk(newCheckinId);
				XbLodger orderMan = daoUtil.lodgerDao.findByPk(newCheckinInfo.getLodgerId());
				List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findValidByCheckinId(newCheckinId);
				XbCheckiner mainCheckiner = null;
				for (XbCheckiner xbCheckiner : checkinerList) {
					if (xbCheckiner.getIsMain() != -1) {// -1表示是下单人,是冗余在xb_checkiner表的,只为了查询"我的订单"接口复杂度简化之用
						if (xbCheckiner.getLodgerId() == newCheckinInfo.getLodgerId()) {// 记得intValue()
							mainCheckiner = xbCheckiner;
							break;
						}
					}
				}
				String idcardNo = orderMan == null ? "" : orderMan.getIdCard();
				String mobile = orderMan == null ? "" : orderMan.getMobile();
				String name = orderMan == null ? "" : orderMan.getLodgerName();
				if (mainCheckiner != null) {
					idcardNo = mainCheckiner.getIdcardNo();
					mobile = mainCheckiner.getMobile();
					name = mainCheckiner.getName();
				}
				if (StringUtils.isBlank(idcardNo) || StringUtils.isBlank(mobile) || StringUtils.isBlank(name)) {
					throw new RuntimeException("续住异常,数据异常,找不到续住的身份证号或姓名或密码,newCheckinId:" + newCheckinId);
				}

				CheckinerUnit main = new CheckinerUnit(AppConstants.Checkiner_sendFlag.SEND_1, idcardNo, mobile, name);
				List<CheckinerUnit> other = null;// 入住阶段添加的其他入住人
				CheckinerVo checkiner = new CheckinerVo(main, other);
				CheckinInputVo checkinInputVo = new CheckinInputVo(newCheckinId, checkiner);

				exceptionHandler.getLog().info(String.format("自动办理入住BEGIN,newCheckinId:%s,oriCheckinId:%s,入参:%s", newCheckinId, oriCheckinId,
						JsonHelper.toJSONString(checkinInputVo)));

				orderMgntService.checkin(checkinInputVo);

				exceptionHandler.getLog().info(String.format("自动办理入住END,newCheckinId:%s,oriCheckinId:%s,入参:%s", newCheckinId, oriCheckinId,
						JsonHelper.toJSONString(checkinInputVo)));
			} else {
				errorMsg = String.format("找不到续住单,原单oriCheckinId:%s", oriCheckinId);
				throw new RuntimeException(errorMsg);
			}
		} catch (Exception e) {
			errorMsg = String.format("【自动办理续住失败】,oriCheckinId:%s,newCheckinId:%s,errorMsg:%s", oriCheckinId, newCheckinId, e.getMessage());
			warningSystemTool.warningLogMultiThread(this.getWarningEntity(e.getMessage()));
		} finally {
			Log_succFlag succFlag = flag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logOverstayAutoCheckin(oriCheckinId, newCheckinId, null, errorMsg, succFlag);
		}
	}
	private WarningEntity getWarningEntity(String errorMsg) {
		WarningEntity warningEntity = new WarningEntity();
		warningEntity.setLogType(2);
		warningEntity.setLogLevel(2);
		warningEntity.setCreateTime(DateUtil.getCurDateTime());
		warningEntity.setLogContent(errorMsg);
		warningEntity.setConfigItemCode("ORD_SYS.OVERSTAY_CHECKIN");
		return warningEntity;
	}
}
