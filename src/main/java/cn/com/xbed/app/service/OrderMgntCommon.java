package cn.com.xbed.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.OperOverstay;
import cn.com.xbed.app.bean.SvSysParam;
import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.AppConstants.LogCouponCard_logType;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.roomstatemodule.CouponCardTool;
import cn.com.xbed.app.service.roomstatemodule.RoomStateModuleBase;
import cn.com.xbed.app.service.roomstatemodule.RoomStateStopBase;
import cn.com.xbed.app.service.roomstatemodule.RoomStateTool;

@Service
@Transactional
public class OrderMgntCommon {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private CommonService commonService;
	@Resource
	private RoomStateTool roomStateTool;
	@Resource
	private RoomStateStopBase roomStateStopBase;
	@Resource
	private RoomStateModuleBase roomStateModuleBase;
	@Resource
	private CouponCardTool couponCardTool;
	@Resource
	private CommonDbLogService commonDbLogService;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderMgntCommon.class));

	
	/**
	 * 锁定卡券,返回是否能够继续
	 * @param orderNo
	 * @param lodgerId
	 * @param cardIdList
	 */
	public Map<String,Object> lockCouponCard(String orderNo, int lodgerId, String cardCode, Date checkinTime, Date checkoutTime, int roomId, int discountPrice, int actualPrice) {
		boolean flag = true;
		try {
			exceptionHandler.getLog().info(String.format("锁定卡券:orderNO:%s, lodgerId:%s, cardCode:%s", orderNo, lodgerId, cardCode));
			return couponCardTool.lockCard(orderNo, lodgerId, cardCode, checkinTime, checkoutTime, roomId, discountPrice, actualPrice);
		} catch (Exception e) {
			flag = false;
			throw exceptionHandler.logServiceException(e);
		} finally {
			Log_succFlag succFlag = flag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logCouponCard("卡操作", orderNo, cardCode, lodgerId, LogCouponCard_logType.CARD_LOCK_1, succFlag);
		}
	}
	
	/**
	 * 解锁卡券
	 * @param orderNo
	 * @param lodgerId
	 * @param cardIdList
	 */
	public void unLockCouponCard(String orderNo, int lodgerId) {
		boolean flag = true;
		String cardId = "";
		try {
			exceptionHandler.getLog().info(String.format("解锁卡券:orderNO:%s, lodgerId:%s", orderNo, lodgerId));
			couponCardTool.unLockCard(orderNo, lodgerId);
		} catch (Exception e) {
			flag = false;
			// 不要抛出异常,存在支付了,并用了卡券(已核销)的情况下,用户还要退款的情况
			exceptionHandler.getLog().info("要抛出异常,存在支付了,并用了卡券(已核销)的情况下,用户还要退款的情况,orderNo:" + orderNo + ",lodgerId:" + lodgerId);
			//throw exceptionHandler.logServiceException(e);
		} finally {
			Log_succFlag succFlag = flag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logCouponCard("卡操作", orderNo, cardId, lodgerId, LogCouponCard_logType.CARD_UNLOCK_2, succFlag);
		}
	}
	
	/**
	 * 核销卡券
	 * @param orderNo
	 * @param lodgerId
	 * @param cardIdList
	 */
	public void destroyCouponCard(String orderNo, int lodgerId) {
		boolean flag = true;
		String cardId = "";
		try {
			exceptionHandler.getLog().info(String.format("核销卡券:orderNO:%s, lodgerId:%s", orderNo, lodgerId));
			couponCardTool.destroyCard(orderNo, lodgerId);
		} catch (Exception e) {
			flag = false;
			throw exceptionHandler.logServiceException(e);
		} finally {
			Log_succFlag succFlag = flag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logCouponCard("卡操作", orderNo, cardId, lodgerId, LogCouponCard_logType.CARD_DESTROY_3, succFlag);
		}
	}
	
//	public void calculateOrderPrice(Date checkinTime, Date checkoutTime, int roomId, List<String> cardIds, int lodgerId) {
//		try {
//			exceptionHandler.getLog().info(String.format("极端订单价格接口券:checkinTime:%s, checkoutTime:%s,roomId:%s, lodgerId:%s,cardIds:%s, lodgerId:%s",
//					DateUtil.getYearMonDayHrMinSecStr_(checkinTime), DateUtil.getYearMonDayHrMinSecStr_(checkoutTime), roomId, cardIds, lodgerId));
//			couponCardTool.calculateOrderPrice(checkinTime, checkoutTime, roomId, cardIds, lodgerId);
//		} catch (Exception e) {
//			throw exceptionHandler.logServiceException("【卡券系统】报错,可能是查询的时间区间",e);
//		}
//	}
//	
	/**
	 * 查询卡券(用订单号)
	 * @param orderNo
	 * @return
	 */
	public List<Map<String, String>> queryCardIdByOrderNo(String orderNo) {
		try {
			exceptionHandler.getLog().info(String.format("核销卡券:orderNO:%s", orderNo));
			return couponCardTool.queryCardIdByOrderNo(orderNo);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	
	
	/**
	 * 查询卡选(用订单ID)
	 * @param orderId
	 * @return
	 */
	public List<Map<String, String>> queryCardIdByOrderNo(int orderId) {
		try {
			exceptionHandler.getLog().info(String.format("核销卡券:orderId:%s", orderId));
			String orderNo = daoUtil.orderMgntDao.transOrderId2orderNo(orderId);
			return this.queryCardIdByOrderNo(orderNo);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 
	 * @param roomId
	 * @return
	 */
	public void transOrder2PaidStatus(int orderId) {
		try {
			XbOrder newOrder = new XbOrder();
			newOrder.setOrderId(orderId);
			newOrder.setStat(AppConstants.Order_stat.PAYED_1);
			int affect = daoUtil.orderMgntDao.updateEntityByPk(newOrder);
			AssertHelper.notZeroNotNegative(affect, "修改订单失败,orderId:" + orderId);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public boolean isCleanRoom(int roomId) {
		try {
			// 如果是脏房不能办理入住
			return roomStateTool.queryCurentRoomStat(roomId) == AppConstants.RoomState_stat.CLEAN_1 ? true : false;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public boolean isDirtyRoom(int roomId) {
		try {
			// 如果是脏房不能办理入住
			return roomStateTool.queryCurentRoomStat(roomId) == AppConstants.RoomState_stat.DIRTY_2 ? true : false;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 
	 * @param loginMobile 下订单的那个帐号的手机号
	 * @param totalPriceFromWebCentUnit 计算出
	 */
	public boolean isTestAccount(String loginMobile) {
		boolean isTestAcct = false;
		try {
			SvSysParam debugSysParam = daoUtil.sysParamDao.getByParamKey("debug.test_lodger");
			String debugStr = debugSysParam.getParamValue();
			exceptionHandler.getLog().info("debug.test_lodger: " + debugStr);
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(debugStr);
			if (obj.getIntValue("mainSwitch") == 1) {// 总开关开启
				JSONArray jsonArr = obj.getJSONArray("mobiles");
				int size = jsonArr.size();
				for (int i = 0; i < size; i++) {
					JSONObject tmp = jsonArr.getJSONObject(i);
					if (loginMobile.equals(tmp.getString("phone")) && tmp.getIntValue("switch") == 1) {
						isTestAcct = true;
					}
				}
			}
		} catch (Exception ex) {
			isTestAcct = false;
		}
		return isTestAcct;
	}
	
	/**
	 * 判断当前入住单是否有已支付的续住单
	 * 
	 * @param checkinId
	 * @return
	 */
	public boolean hasPaidOverstay(int checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			return this.hasPaidOverstay(checkinInfo);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 判断当前入住单是否有已支付的续住单
	 * @param checkinInfo
	 * @return
	 */
	public boolean hasPaidOverstay(XbCheckin checkinInfo) {
		boolean hasPaidOverstay = false;
		exceptionHandler.getLog().info(String.format("当前入住单有已支付的续住单?checkinId:%s", checkinInfo.getCheckinId()));
		try {
			if (checkinInfo.getOverstayFlag() == AppConstants.Checkin_overstayFlag.ORI_ROOM_1) {
				exceptionHandler.getLog().info(String.format("1,checkinId:%s", checkinInfo.getCheckinId()));
				OperOverstay operOverstay = daoUtil.operOverstayDao.findLatestByOriCheckinId(checkinInfo.getCheckinId());
				if (operOverstay != null) {
					exceptionHandler.getLog().info(String.format("2,checkinId:%s", checkinInfo.getCheckinId()));
					XbOrder orderInfo = daoUtil.orderMgntDao.findOrderByCheckinId(operOverstay.getNewCheckinId());
					if (orderInfo != null && orderInfo.getStat() == AppConstants.Order_stat.PAYED_1) {
						exceptionHandler.getLog().info(String.format("3,checkinId:%s", checkinInfo.getCheckinId()));
						hasPaidOverstay = true;
					}
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
		exceptionHandler.getLog().info(String.format("判断当前入住单是否有续住的入住单,checkinId:%s, hasOverstay:%s", checkinInfo.getCheckinId(), hasPaidOverstay));
		return hasPaidOverstay;
	}

	/**
	 * 判断是否存在已支付或未支付的续住单(取消必须排除)
	 * @param checkinInfo
	 * @return
	 */
	public boolean hasPaidOrUnpaidOverstay(XbCheckin checkinInfo) {
		boolean hasPaidOrUnpaidOverstay = false;
		exceptionHandler.getLog().info(String.format("当前入住单有未支付或已支付的续住单?checkinId:%s", checkinInfo.getCheckinId()));
		try {
			if (checkinInfo.getOverstayFlag() == AppConstants.Checkin_overstayFlag.ORI_ROOM_1) {
				exceptionHandler.getLog().info(String.format("1,checkinId:%s", checkinInfo.getCheckinId()));
				OperOverstay operOverstay = daoUtil.operOverstayDao.findLatestByOriCheckinId(checkinInfo.getCheckinId());
				if (operOverstay != null) {
					exceptionHandler.getLog().info(String.format("2,checkinId:%s", checkinInfo.getCheckinId()));
					XbOrder orderInfo = daoUtil.orderMgntDao.findOrderByCheckinId(operOverstay.getNewCheckinId());
					if (orderInfo != null && orderInfo.getStat() == AppConstants.Order_stat.PAYED_1 || orderInfo != null && orderInfo.getStat() == AppConstants.Order_stat.NEW_0) {
						exceptionHandler.getLog().info(String.format("3,checkinId:%s", checkinInfo.getCheckinId()));
						hasPaidOrUnpaidOverstay = true;
					}
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
		exceptionHandler.getLog().info(String.format("判断当前入住单是否有续住的入住单,checkinId:%s, hasOverstay:%s", checkinInfo.getCheckinId(), hasPaidOrUnpaidOverstay));
		return hasPaidOrUnpaidOverstay;
	}
	
	/**
	 * 判断是否存在已支付或未支付的续住单(取消必须排除)
	 * @param checkinId
	 * @return
	 */
	public boolean hasPaidOrUnpaidOverstay(int checkinId) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			return this.hasPaidOrUnpaidOverstay(checkinInfo);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	/**
	 * 查询原单的续住新单
	 * @param oriCheckinId
	 * @return 返回null表示找不到
	 */
	public Integer queryOverstayCheckinId(int oriCheckinId) {
		exceptionHandler.getLog().info(String.format("查询续住入住单,oriCheckinId:%s", oriCheckinId));
		try {
			Integer newCheckinId = null;
			OperOverstay operOverstay = daoUtil.operOverstayDao.findLatestByOriCheckinId(oriCheckinId);
			exceptionHandler.getLog().info(operOverstay);
			if (operOverstay != null) {
				newCheckinId = operOverstay.getNewCheckinId();
			}
			exceptionHandler.getLog().info(String.format("判断当前入住单是否有续住的入住单,oriCheckinId:%s, newCheckinId:%s", oriCheckinId, newCheckinId));
			return newCheckinId;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 
	 * @param newCheckinId
	 * @return
	 */
	public boolean checkCanOverstayCheckin(int newCheckinId) {
		exceptionHandler.getLog().info(String.format("检查是否能续住办理入住,newCheckinId:%s", newCheckinId));
		boolean canCheckin = true;
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(newCheckinId);
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			if (orderInfo.getStat() != AppConstants.Order_stat.PAYED_1) {
				
			}
		} catch (Exception e) {
			canCheckin = false;
		}
		return canCheckin;
	}
	
	/**
	 * 由新的newCheckinId查出续住之前的旧入住单id <br>
	 * 
	 * 不抛出异常
	 * 
	 * @param newCheckinId
	 * @return 
	 */
	public Integer queryOriCheckinIdByNewCheckinId(int newCheckinId) {
		exceptionHandler.getLog().info(String.format("检查是否能续住办理入住,newCheckinId:%s", newCheckinId));
		Integer oriCheckinId = null;
		try {
			OperOverstay operOverstay = daoUtil.operOverstayDao.findLatestByNewCheckinId(newCheckinId);
			if (operOverstay != null) {
				oriCheckinId = operOverstay.getOriCheckinId();
			}
		} catch (Exception e) {
			oriCheckinId = null;
		}
		return oriCheckinId == null ? -1 : oriCheckinId;
	}
	
	/**
	 * 
	 * @param checkinId
	 * @return
	 */
//	public boolean isOverstayCheckin(int checkinId) {
//		exceptionHandler.getLog().info(String.format("判断当前是否续住单,checkinId:%s", checkinId));
//		boolean result = false;
//		try {
//			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
//			if (checkinInfo.getOverstayFlag() == AppConstants.Checkin_overstayFlag.NEW_ROOM_2) {
//				result = true;
//			}
//		} catch (Exception e) {
//			result = false;
//		}
//		exceptionHandler.getLog().info(String.format("判断当前是否续住单,result:%s", result));
//		return result;
//	}
	
	public boolean isOverstayCheckin(int overstayFlag) {
		exceptionHandler.getLog().info(String.format("判断当前是否续住单,overstayFlag:%s", overstayFlag));
		boolean result = false;
		try {
			if (overstayFlag == AppConstants.Checkin_overstayFlag.NEW_ROOM_2) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		exceptionHandler.getLog().info(String.format("判断当前是否续住单,result:%s", result));
		return result;
	}
	
	
	/**
	 * 获得订单渠道值
	 * 
	 * @param channel
	 * @return
	 */
	public int getOtaChannel(int channel) {
		switch (channel) {
		case 1:// 携程
			return AppConstants.Order_source.CTRIP_5;
		case 2:
			return AppConstants.Order_source.MEITUAN_7;
		default:
			throw new RuntimeException("渠道类型传错");
		}
	}
}
