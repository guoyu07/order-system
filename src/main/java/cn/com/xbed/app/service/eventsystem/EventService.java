package cn.com.xbed.app.service.eventsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CommonDbLogService;
import cn.com.xbed.app.service.LodgerService;
import cn.com.xbed.app.service.WarningSystemTool;
import cn.com.xbed.app.service.ljh.CleanSystemService;

/**
 * 该类存放抛出事件的逻辑<br>
 * 例如: 预订成功/办理入住/未支付警告/未支付超时取消提醒/...
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class EventService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(EventService.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private WarningSystemTool warningSystemTool;
	@Resource
	private CommonDbLogService commonDbLogService;
	@Resource
	private CleanSystemService cleanSystemService;
	@Resource
	private LodgerService lodgerService;

	
	
	
	/**
	 * 抛出事件: 主动办理退房<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param checkinId
	 */
	public void throwOverstayCheckoutEvent(int checkinId) {
		boolean succFlag = true;
		String eventCode = EventCode.OVERSTAY_CHECKOUT;
		try {
			exceptionHandler.getLog().info("throwOverstayCheckoutEvent,checkinId:" + checkinId);
			Map<String, Object> params = this.getCheckinInfo(checkinId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwOverstayCheckoutEvent错误," + checkinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 续住退房事件", EventCode.LogEventThrow_logType.OVERSTAY_CHECKOUT, "", enumSuccFlag, null, checkinId,
					null, eventCode);
		}
	}
	
	
	public void throwResendOpenPwdEvent(int checkinId, String mobile) {
		boolean succFlag = true;
		String eventCode = EventCode.RESEND_OPEN_PWD;
		try {
			exceptionHandler.getLog().info("throwResendOpenPwdEvent,checkinId:" + checkinId);
			Map<String, Object> params = new LinkedHashMap<>(2);
			Map<String, Object> oriCheckinInfo = this.getCheckinInfo(checkinId);
			params.put("oriCheckinInfo", oriCheckinInfo);
			params.put("mobile", mobile);
			params.put("openId", lodgerService.findOpenIdByMobileOrLodgerId(null, mobile));
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwResendOpenPwdEvent错误," + checkinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 重发开门密码", EventCode.LogEventThrow_logType.RESEND_OPEN_PWD, "", enumSuccFlag, null, checkinId,
					null, eventCode);
		}
	}
	
	
	/**
	 * 抛出事件: 在住申请
	 * @param checkinId
	 * @param period
	 */
	public void throwApplyCleanEvent(int checkinId, int period) {
		boolean succFlag = true;
		String eventCode = EventCode.APPLY_CLEAN;
		try {
			exceptionHandler.getLog().info("throwApplyCleanEvent,checkinId:" + checkinId);
			Map<String, Object> params = this.getApplyCleanInfo(checkinId, period);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwApplyCleanEvent错误," + checkinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 在住申请打扫", EventCode.LogEventThrow_logType.APPLY_CLEAN, "", enumSuccFlag, null, checkinId,
					null, eventCode);
		}
	}
	
	public void throwCheckoutNotifyEvent(int checkinId) {
		boolean succFlag = true;
		String eventCode = EventCode.CHECKOUT_NOTIFY;
		try {
			exceptionHandler.getLog().info("throwCheckoutNotifyEvent,checkinId:" + checkinId);
			Map<String, Object> params = this.getCheckoutNotifyInfo(checkinId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwCheckoutNotifyEvent错误," + checkinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 退房通知运营人员", EventCode.LogEventThrow_logType.CHECKOUT_NOTIFY, "", enumSuccFlag, null, checkinId,
					null, eventCode);
		}
	}
	
	public Map<String, Object> getCheckoutNotifyInfo(int checkinId) {
		try {
			Map<String, Object> map = new LinkedHashMap<>();
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
			XbLodger mainCheckinMan = daoUtil.lodgerDao.findByPk(checkinInfo.getLodgerId());
			List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findByCheckinId(checkinInfo.getCheckinId());
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbLodger orderMan = daoUtil.lodgerDao.findByPk(orderInfo.getLodgerId());

			// 获得需要通知的运营人员
			map.put("operationPhones", cleanSystemService.getOperationManPhones());
						
			map.put("orderInfo", orderInfo);
			map.put("orderMan", orderMan);
			map.put("checkinInfo", checkinInfo);
			map.put("mainCheckinMan", mainCheckinMan);
			map.put("roomInfo", roomInfo);
			map.put("chainInfo", chainInfo);
			map.put("checkinerList", checkinerList);
			map.put("orderType", cleanSystemService.getOrderTypeDesci(orderInfo.getOrderType()));
			map.put("timestamp", DateUtil.getCurDateTimeStr());
			map.put("currentTime", DateUtil.getYearMonDayHrMinSecStr_(DateUtil.getCurDateTime()));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	public Map<String, Object> getApplyCleanInfo(int checkinId, int period) {
		try {
			Map<String, Object> map = new LinkedHashMap<>();
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
			XbLodger mainCheckinMan = daoUtil.lodgerDao.findByPk(checkinInfo.getLodgerId());
			List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findByCheckinId(checkinInfo.getCheckinId());
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbLodger orderMan = daoUtil.lodgerDao.findByPk(orderInfo.getLodgerId());

			// 获得需要通知的运营人员
			map.put("operationPhones", cleanSystemService.getOperationManPhones());
			
			map.put("orderInfo", orderInfo);
			map.put("orderMan", orderMan);
			map.put("checkinInfo", checkinInfo);
			map.put("mainCheckinMan", mainCheckinMan);
			map.put("roomInfo", roomInfo);
			map.put("chainInfo", chainInfo);
			map.put("checkinerList", checkinerList);
			map.put("period", cleanSystemService.getCleanApplyPeriod(period));
			map.put("orderType", cleanSystemService.getOrderTypeDesci(orderInfo.getOrderType()));
			map.put("timestamp", DateUtil.getCurDateTimeStr());
			map.put("currentTime", DateUtil.getYearMonDayHrMinSecStr_(DateUtil.getCurDateTime()));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 抛出事件: 未支付订单警告尽快支付<br>
	 * 不抛出异常!不影响主业务
	 * 
	 * @param orderId
	 */
	public void throwWarningUnpaidEvent(int orderId) {
		boolean succFlag = true;
		String eventCode = EventCode.WARNING_UNPAID_20_MIN;
		try {
			exceptionHandler.getLog().info("throwWarningUnpaidEvent," + orderId);
			Map<String, Object> params = this.getOrderInfo(orderId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwWarningUnpaidEvent错误," + orderId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent("抛出事件: 未支付订单警告尽快支付", EventCode.LogEventThrow_logType.WARNING_UNPAID_20_MIN_1, "", enumSuccFlag, orderId, null,
					null, eventCode);
		}
	}

	/**
	 * 抛出事件: 超时未支付取消通知<br>
	 * 不抛出异常!不影响主业务
	 * 
	 * @param orderId
	 */
	public void throwCancelUnpaidEvent(int orderId) {
		boolean succFlag = true;
		String eventCode = EventCode.CANCEL_UNPAID_30_MIN;
		try {
			exceptionHandler.getLog().info("throwCancelUnpaidEvent," + orderId);
			Map<String, Object> params = this.getOrderInfo(orderId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwCancelUnpaidEvent错误," + orderId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent("抛出事件: 超时未支付取消通知", EventCode.LogEventThrow_logType.CANCEL_UNPAID_30_MIN_2, "", enumSuccFlag, orderId, null,
					null, eventCode);
		}
	}

	/**
	 * 抛出事件: 预退房 <br>
	 * 不抛出异常!不影响主业务
	 * 
	 * @param checkinId
	 */
	public void throwPreCheckoutEvent(int checkinId) {
		boolean succFlag = true;
		String eventCode = EventCode.PRE_CHECKOUT_HINT;
		try {
			exceptionHandler.getLog().info("throwCancelUnpaidEvent," + checkinId);
			Map<String, Object> params = this.getCheckinInfo(checkinId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwPreCheckoutEvent错误," + checkinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 预退房", EventCode.LogEventThrow_logType.PRE_CHECKOUT_HINT_3, "", enumSuccFlag, null, checkinId, null,
					eventCode);
		}
	}

	/**
	 * 抛出事件: 预订后支付成功<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param orderId
	 */
	public void throwBookAfterPaySuccEvent(int orderId) {
		boolean succFlag = true;
		String eventCode = EventCode.BOOK_AFTER_PAY_SUCC;
		try {
			exceptionHandler.getLog().info("throwBookAfterPaySuccEvent," + orderId);
			Map<String, Object> params = this.getOrderInfo(orderId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwBookAfterPaySuccEvent错误," + orderId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 订购支付后", EventCode.LogEventThrow_logType.BOOK_AFTER_PAY_SUCC, "", enumSuccFlag, orderId, null, null,
					eventCode);
		}
	}

	/**
	 * 业主下单
	 * @param orderId
	 */
	public void throwOwnerBookEvent(int orderId) {
		boolean succFlag = true;
		String eventCode = EventCode.OWNER_BOOK;
		try {
			exceptionHandler.getLog().info("throwOwnerBookEvent," + orderId);
			Map<String, Object> params = this.getOrderInfo(orderId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwOwnerBookEvent错误," + orderId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 订购支付后", EventCode.LogEventThrow_logType.OWNER_BOOK, "", enumSuccFlag, orderId, null, null,
					eventCode);
		}
	}
	
	/**
	 * 抛出事件: 用户主动取消订单<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param orderId
	 */
	public void throwCustCancelOrderEvent(int orderId) {
		boolean succFlag = true;
		String eventCode = EventCode.CUST_CANCEL_ORDER;
		try {
			exceptionHandler.getLog().info("throwCustCancelOrderEvent,orderId:" + orderId);
			Map<String, Object> params = this.getOrderInfo(orderId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwCustCancelOrderEvent错误," + orderId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 用户主动取消订单", EventCode.LogEventThrow_logType.CUST_CANCEL_ORDER, "", enumSuccFlag, orderId, null, null,
					eventCode);
		}
	}

	/**
	 * 抛出事件: 办理入住<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param checkinId
	 */
	public void throwCheckinHintEvent(int checkinId) {
		boolean succFlag = true;
		String eventCode = EventCode.CHECKIN_HINT;
		try {
			exceptionHandler.getLog().info("throwCheckinHintEvent,checkinId:" + checkinId);
			Map<String, Object> params = this.getCheckinInfo(checkinId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwCheckinHintEvent错误," + checkinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 办理入住", EventCode.LogEventThrow_logType.CHECKIN_HINT, "", enumSuccFlag, null, checkinId, null,
					eventCode);
		}
	}

	/**
	 * 抛出事件: 主动办理退房<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param checkinId
	 */
	public void throwActiveCheckoutHintEvent(int checkinId) {
		boolean succFlag = true;
		String eventCode = EventCode.ACTIVE_CHECKOUT_HINT;
		try {
			exceptionHandler.getLog().info("throwActiveCheckoutHintEvent,checkinId:" + checkinId);
			Map<String, Object> params = this.getCheckinInfo(checkinId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwActiveCheckoutHintEvent错误," + checkinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 主动办理退房", EventCode.LogEventThrow_logType.ACTIVE_CHECKOUT_HINT, "", enumSuccFlag, null, checkinId,
					null, eventCode);
		}
	}

	/**
	 * 抛出事件: 进程自动退房<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param checkinId
	 */
	public void throwAutoCheckoutHintEvent(int checkinId) {
		boolean succFlag = true;
		String eventCode = EventCode.AUTO_CHECKOUT_HINT;
		try {
			exceptionHandler.getLog().info("throwAutoCheckoutHintEvent,checkinId:" + checkinId);
			Map<String, Object> params = this.getCheckinInfo(checkinId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwAutoCheckoutHintEvent错误," + checkinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: 进程办理退房", EventCode.LogEventThrow_logType.AUTO_CHECKOUT_HINT, "", enumSuccFlag, null, checkinId, null,
					eventCode);
		}
	}

	/**
	 * 抛出事件: OMS取消订单(未支付)<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param orderId
	 */
	public void throwOMSCancelUnpaidOrderEvent(int orderId) {
		boolean succFlag = true;
		String eventCode = EventCode.OMS_CANCEL_UNPAID;
		try {
			exceptionHandler.getLog().info("throwOMSCancelUnpaidOrderEvent,orderId:" + orderId);
			Map<String, Object> params = this.getOrderInfo(orderId, false);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwOMSCancelUnpaidOrderEvent错误," + orderId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: OMS取消订单(未支付)", EventCode.LogEventThrow_logType.OMS_CANCEL_UNPAID, "", enumSuccFlag, orderId, null,
					null, eventCode);
		}
	}

	/**
	 * 抛出事件: OMS取消订单(已支付)<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param orderId
	 */
	public void throwOMSCancelPaidOrderEvent(int orderId) {
		boolean succFlag = true;
		String eventCode = EventCode.OMS_CANCEL_PAID;
		try {
			exceptionHandler.getLog().info("throwOMSCancelPaidOrderEvent,orderId:" + orderId);
			Map<String, Object> params = this.getOrderInfo(orderId, false);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwOMSCancelPaidOrderEvent错误," + orderId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logEvent(" 抛出事件: OMS取消订单(未支付)", EventCode.LogEventThrow_logType.OMS_CANCEL_PAID, "", enumSuccFlag, orderId, null, null,
					eventCode);
		}
	}

	/**
	 * 抛出事件: 换房<br>
	 * 
	 * 不抛出异常!不影响主业务
	 * 
	 * @param checkinId
	 */
	public void throwChgRoomEvent(int oriCheckinId, int newCheckinId) {
		boolean succFlag = true;
		String eventCode = EventCode.CHG_ROOM;
		try {
			exceptionHandler.getLog().info("throwChgRoomEvent,oriCheckinId:" + oriCheckinId + ",newCheckinId:" + newCheckinId);
			Map<String, Object> params = new LinkedHashMap<>(2);
			Map<String, Object> oriCheckinInfo = this.getCheckinInfo(oriCheckinId);
			Map<String, Object> newCheckinInfo = this.getCheckinInfo(newCheckinId);
			params.put("oriCheckinInfo", oriCheckinInfo);
			params.put("newCheckinInfo", newCheckinInfo);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwChgRoomEvent错误,oriCheckinId:" + oriCheckinId + ",newCheckinId:" + newCheckinId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			String detailDesc = "checkinId: " + oriCheckinId + " 换成 " + newCheckinId;
			commonDbLogService.logEvent(" 抛出事件: 进程办理退房", EventCode.LogEventThrow_logType.CHG_ROOM, detailDesc, enumSuccFlag, null, newCheckinId, null,
					eventCode);
		}
	}

	/**
	 * 帮忙注册是帐号
	 * 
	 * @param lodgerId
	 */
	public void throwRegisterUserEvent(int lodgerId) {
		boolean succFlag = true;
		String eventCode = EventCode.REGISTER_USER;
		try {
			exceptionHandler.getLog().info("throwRegisterUserEvent,lodgerId:" + lodgerId);
			Map<String, Object> params = this.getLodgerInfo(lodgerId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwRegisterUserEvent错误,lodgerId:" + lodgerId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			String detailDesc = "新lodgerId:" + lodgerId;
			commonDbLogService.logEvent(" 抛出事件: 进程办理退房", EventCode.LogEventThrow_logType.REGISTER_USER, detailDesc, enumSuccFlag, null, null,
					lodgerId, eventCode);
		}
	}

	/**
	 * 帮忙注册是帐号(业主)
	 * 
	 * @param lodgerId
	 */
	public void throwRegisterOwnerEvent(int lodgerId) {
		boolean succFlag = true;
		String eventCode = EventCode.REGISTER_OWNER;
		try {
			exceptionHandler.getLog().info("throwRegisterOwnerEvent,lodgerId:" + lodgerId);
			Map<String, Object> params = this.getLodgerInfo(lodgerId);
			sendEvent(eventCode, params);
		} catch (Exception e) {
			succFlag = false;
			exceptionHandler.getLog().error("【事件系统】throwRegisterOwnerEvent错误,lodgerId:" + lodgerId);
		} finally {
			Log_succFlag enumSuccFlag = succFlag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			String detailDesc = "新lodgerId:" + lodgerId;
			commonDbLogService.logEvent(" 抛出事件: 进程办理退房", EventCode.LogEventThrow_logType.REGISTER_OWNER, detailDesc, enumSuccFlag, null, null,
					lodgerId, eventCode);
		}
	}
	
	
	
	/**
	 * 获得lodger信息
	 * 
	 * @param lodgerId
	 * @return
	 */
	public Map<String, Object> getLodgerInfo(int lodgerId) {
		try {
			Map<String, Object> map = new LinkedHashMap<>();
			XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(lodgerId);
			map.put("lodgerInfo", lodgerInfo);
			map.put("currentTime", DateUtil.getYearMonDayHrMinSecStr_(DateUtil.getCurDateTime()));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 获得入住单相关信息
	 * 
	 * @param checkinId
	 * @return
	 */
	public Map<String, Object> getCheckinInfo(int checkinId) {
		try {
			Map<String, Object> map = new LinkedHashMap<>();
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
			XbLodger mainCheckinMan = daoUtil.lodgerDao.findByPk(checkinInfo.getLodgerId());
			List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findByCheckinId(checkinInfo.getCheckinId());
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(checkinInfo.getOrderId());
			XbLodger orderMan = daoUtil.lodgerDao.findByPk(orderInfo.getLodgerId());
			List<JSONObject> checkinerListExt = new ArrayList<>();
			for (XbCheckiner xbCheckiner : checkinerList) {
				String sql = "select open_id from xb_lodger where lodger_id=?";
				List<Map<String, Object>> mapList = daoUtil.lodgerDao.queryMapList(sql, new Object[] { xbCheckiner.getLodgerId() }, true);
				JSONObject obj = JsonHelper.parseJSONStr2JSONObject(JsonHelper.toJSONString(xbCheckiner));
				if (!mapList.isEmpty()) {
					String openId = (String) mapList.get(0).get("open_id");
					obj.put("openId", openId);
				}
				checkinerListExt.add(obj);
			}
			map.put("orderInfo", orderInfo);
			map.put("orderMan", orderMan);
			map.put("checkinInfo", checkinInfo);
			map.put("mainCheckinMan", mainCheckinMan);
			map.put("roomInfo", roomInfo);
			map.put("chainInfo", chainInfo);
			map.put("checkinerList", checkinerListExt);
			map.put("currentTime", DateUtil.getYearMonDayHrMinSecStr_(DateUtil.getCurDateTime()));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 发送事件到事件系统
	 * 
	 * @param eventCode
	 * @param params
	 */
	protected void sendEvent(String eventCode, Map<String, Object> params) {
		try {
			Map<String, Object> mqParams = new HashMap<>();
			mqParams.put("eventCode", eventCode);
			mqParams.put("parameters", params);
			String input = JsonHelper.toJSONString(mqParams);
			exceptionHandler.getLog().info("【MQ系统】发送事件" + input);
			warningSystemTool.sendQueueMessage("xbed.common.event", input);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("【MQ系统】发送事件失败", e);
		}
	}

	/**
	 * 获得某个订单的信息
	 * 
	 * @param orderId
	 * @return
	 */
	public Map<String, Object> getOrderInfo(int orderId, boolean... containChgRoomOriRooom) {
		try {
			Map<String, Object> map = new LinkedHashMap<>();
			XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
			map.put("orderInfo", orderInfo);
			XbLodger orderMan = daoUtil.lodgerDao.findByPk(orderInfo.getLodgerId());
			map.put("orderMan", orderMan);

			boolean containChgRoomOri = true;
			if (containChgRoomOriRooom.length > 0) {
				containChgRoomOri = containChgRoomOriRooom[0];
			}
			List<Map<String, Object>> list = new ArrayList<>();
			List<XbCheckin> checkList = daoUtil.checkinDao.findByOrderId(orderId);
			for (XbCheckin checkinInfo : checkList) {
				if (checkinInfo.getChgRoomFlag() == AppConstants.Checkin_chgRoomFlag.ORI_ROOM_1 && !containChgRoomOri) {// 是换房原单,并且不想包含的时候就跳过
					continue;
				}
				Map<String, Object> checkinInfoRoomInfo = new LinkedHashMap<>();
				XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
				XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
				XbLodger mainCheckinMan = daoUtil.lodgerDao.findByPk(checkinInfo.getLodgerId());
				checkinInfoRoomInfo.put("checkinInfo", checkinInfo);
				checkinInfoRoomInfo.put("mainCheckinMan", mainCheckinMan);
				checkinInfoRoomInfo.put("roomInfo", roomInfo);
				checkinInfoRoomInfo.put("chainInfo", chainInfo);
				List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findByCheckinId(checkinInfo.getCheckinId());
				List<JSONObject> checkinerListExt = new ArrayList<>();
				for (XbCheckiner xbCheckiner : checkinerList) {
					String sql = "select open_id from xb_lodger where lodger_id=?";
					List<Map<String, Object>> mapList = daoUtil.lodgerDao.queryMapList(sql, new Object[] { xbCheckiner.getLodgerId() }, true);
					JSONObject obj = JsonHelper.parseJSONStr2JSONObject(JsonHelper.toJSONString(xbCheckiner));
					if (!mapList.isEmpty()) {
						String openId = (String) mapList.get(0).get("open_id");
						obj.put("openId", openId);
					}
					checkinerListExt.add(obj);
				}
				checkinInfoRoomInfo.put("checkinerList", checkinerListExt);
				list.add(checkinInfoRoomInfo);
			}
			map.put("checkinInfo_roomInfo_chainInfo", list);
			map.put("currentTime", DateUtil.getYearMonDayHrMinSecStr_(DateUtil.getCurDateTime()));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

}
