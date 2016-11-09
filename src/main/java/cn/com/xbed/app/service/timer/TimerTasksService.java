package cn.com.xbed.app.service.timer;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbOrderStop;
import cn.com.xbed.app.bean.vo.CheckinInputVo;
import cn.com.xbed.app.bean.vo.CheckinerUnit;
import cn.com.xbed.app.bean.vo.CheckinerVo;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CommonDbLogService;
import cn.com.xbed.app.service.OrderMgntCommon;
import cn.com.xbed.app.service.OrderMgntService;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.ordermodule.impl.CancelType;
import cn.com.xbed.app.service.ordermodule.impl.CheckoutType;
import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.service.roomstatemodule.RoomStateStopBase;
import cn.com.xbed.app.service.smsmodule.CommonSms;

@Service
public class TimerTasksService {
	@Resource
	private DaoUtil daoUtil;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(TimerTasksService.class));
	@Resource
	private CommonSms commonSms;
	@Resource
	private OrderUtil orderUtil;
	@Resource
	private RoomStateStopBase roomStateStopBase;
	@Resource
	private EventService eventService;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	@Resource
	private OrderMgntService orderMgntService;
	@Resource
	private CommonDbLogService commonDbLogService;
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleOverstayCheckin(int checkinId) {
		boolean flag = true;
		String errorMsg = "";
		Integer oriCheckinId = null;
		try {
			exceptionHandler.getLog().info("处理续住办理入住,checkinId:" + checkinId);

			oriCheckinId = orderMgntCommon.queryOriCheckinIdByNewCheckinId(checkinId);// 查出原来的入住单号,为了记录日志用
			
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);// 要办理入住的入住单
			
			XbLodger checkinMan = daoUtil.lodgerDao.findByPk(checkinInfo.getLodgerId());// 主入住人
			
			// 获得身份证号,手机和名字
			List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findValidByCheckinId(checkinId);
			XbCheckiner mainCheckiner = null;
			for (XbCheckiner xbCheckiner : checkinerList) {
				if (xbCheckiner.getIsMain() != -1) {// -1表示是下单人,是冗余在xb_checkiner表的,只为了查询"我的订单"接口复杂度简化之用
					if (xbCheckiner.getCheckinId() == checkinId) {
						mainCheckiner = xbCheckiner;// 找出主入住人
					}
				}
			}
			String idcardNo = checkinMan == null ? "" : checkinMan.getIdCard();
			String mobile = checkinMan == null ? "" : checkinMan.getMobile();
			String name = checkinMan == null ? "" : checkinMan.getLodgerName();
			if (mainCheckiner != null) {
				idcardNo = mainCheckiner.getIdcardNo();
				mobile = mainCheckiner.getMobile();
				name = mainCheckiner.getName();
			}
			if (StringUtils.isBlank(idcardNo) || StringUtils.isBlank(mobile) || StringUtils.isBlank(name)) {
				throw new RuntimeException("续住异常,数据异常,找不到续住的身份证号或姓名或密码,checkinId:" + checkinId);
			}

			// 组装入参
			CheckinerUnit main = new CheckinerUnit(AppConstants.Checkiner_sendFlag.SEND_1, idcardNo, mobile, name);
			List<CheckinerUnit> other = null;// 入住阶段添加的其他入住人
			CheckinerVo checkiner = new CheckinerVo(main, other);
			CheckinInputVo checkinInputVo = new CheckinInputVo(checkinId, checkiner);

			exceptionHandler.getLog().info(String.format("自动办理入住BEGIN,newCheckinId:%s,oriCheckinId:%s,入参:%s", checkinId, oriCheckinId,
					JsonHelper.toJSONString(checkinInputVo)));

			orderMgntService.checkin(checkinInputVo);

			exceptionHandler.getLog().info(String.format("自动办理入住END,newCheckinId:%s,oriCheckinId:%s,入参:%s", checkinId, oriCheckinId,
					JsonHelper.toJSONString(checkinInputVo)));
		} catch (Exception e) {
			flag = false;
			errorMsg = String.format("【自动办理续住失败】,oriCheckinId:%s,newCheckinId:%s,errorMsg:%s", oriCheckinId, checkinId, e.getMessage());
			exceptionHandler.logActionException("处理续住办理入住失败", e);// 不要抛出异常！！！
		} finally {
			Log_succFlag succFlag = flag ? Log_succFlag.SUCC : Log_succFlag.FAIL;
			commonDbLogService.logOverstayAutoCheckin(oriCheckinId, checkinId, "扫描进程处理", errorMsg, succFlag);
		}
	}
	
	
	
	public enum OperType {
		BEGIN,END
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void autoBeginAndEndStop(int stopId, OperType operType) {
		try {
			switch (operType) {
			case BEGIN: {
				exceptionHandler.getLog().info("自动进程处理,stopId:" + stopId + ",停用开始");
				roomStateStopBase.roomStateStopBegin(stopId);
				XbOrderStop orderStop = new XbOrderStop();
				orderStop.setStopId(stopId);
				orderStop.setStopStat(AppConstants.OrderStop_stopStat.BEGIN_1);
				orderStop.setActualStopBegin(DateUtil.getCurDateTime());
				daoUtil.orderStopDao.updateEntityByPk(orderStop);
			}
				break;
			case END: {
				exceptionHandler.getLog().info("自动进程处理,stopId:" + stopId + ",停用结束");
				roomStateStopBase.roomStateStopEnd(stopId);
				XbOrderStop orderStop = new XbOrderStop();
				orderStop.setStopId(stopId);
				orderStop.setStopStat(AppConstants.OrderStop_stopStat.END_2);
				orderStop.setActualStopEnd(DateUtil.getCurDateTime());
				daoUtil.orderStopDao.updateEntityByPk(orderStop);
			}
				break;
			default:
				throw new RuntimeException("类型错误");
			}
		} catch (Exception e) {
			exceptionHandler.logActionException("自动处理停用单失败", e);// 不要抛出异常！！！
		}
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void autoCheckout(int checkinId) {
		try {
			exceptionHandler.getLog().info("自动进程处理退房,checkinId:" + checkinId);

			int checkoutType = AppConstants.Checkin_checkoutType.TIMEOUT_CHECKOUT_2;
			orderUtil.checkout(checkinId, checkoutType, CheckoutType.AUTO);
		} catch (Exception e) {
			exceptionHandler.logActionException("发送订单支付超时预警短信失败", e);// 不要抛出异常！！！
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void notifyUnpaidUser(int orderId) {
		try {
			exceptionHandler.getLog().info("支付预警短信,orderId:" + orderId);

			// 发送警告短信
			//int smsId = commonSms.sendNotifyUnpaidSms(orderId);
			// 抛事件
			eventService.throwWarningUnpaidEvent(orderId);
			
			// 更新已处理标记
			XbOrder newOrder = new XbOrder();
			newOrder.setOrderId(orderId);
			newOrder.setExHold1("由事件系统发送");
			daoUtil.orderMgntDao.updateEntityByPk(newOrder);
		} catch (Exception e) {
			exceptionHandler.logActionException("发送订单支付超时预警短信失败", e);// 不要抛出异常！！！
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void cancelUnpaid(int orderId) {
		try {
			exceptionHandler.getLog().info("超时取消扫描,orderId:" + orderId);
			orderUtil.cancel(orderId, CancelType.AUTO_PROCESS);

			// 更新已处理标记和订单状态
			XbOrder newOrder = new XbOrder();
			newOrder.setOrderId(orderId);
			newOrder.setExHold2("由事件系统发送");
			newOrder.setStat(AppConstants.Order_stat.TIMEOUT_3);
			daoUtil.orderMgntDao.updateEntityByPk(newOrder);
		} catch (Exception e) {
			exceptionHandler.logActionException("发送订单支付超时预警短信失败", e);// 不要抛出异常！！！
		}
	}

	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void preCheckoutHint(int checkinId) {
		try {
			exceptionHandler.getLog().info("预退房提醒,checkinId:" + checkinId);
			if (orderMgntCommon.hasPaidOverstay(checkinId)) {
				// 不处理
				exceptionHandler.getLog().info("预退房提醒,原单有续住单,不抛事件,checkinId:" + checkinId);
			} else {
				eventService.throwPreCheckoutEvent(checkinId);
			}

			// 更新已处理标记和订单状态
			XbCheckin newCheckin = new XbCheckin();
			newCheckin.setCheckinId(checkinId);
			newCheckin.setPrecheckoutFlag(AppConstants.Checkin_precheckoutFlag.HANDLED_1);
			daoUtil.checkinDao.updateEntityByPk(newCheckin);
		} catch (Exception e) {
			exceptionHandler.logActionException("预退房提醒失败", e);// 不要抛出异常！！！
		}
	}
}
