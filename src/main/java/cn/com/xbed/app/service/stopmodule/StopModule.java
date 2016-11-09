package cn.com.xbed.app.service.stopmodule;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.LogStopSheet;
import cn.com.xbed.app.bean.XbOrderStop;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.bean.vo.CalendarUnit;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.roomstatemodule.RoomStateStopBase;
import cn.com.xbed.app.service.stopmodule.vo.StopEntity;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.RandomUtil;

/**
 * 简单起见不用模版方法模式实现
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class StopModule {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private RoomStateStopBase roomStateStopBase;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(StopModule.class));

	// 新建停用单
	public XbOrderStop addStopSheet(StopEntity stopEntity) {
		try {
			// 保存xb_order_stop
			XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(stopEntity.getRoomId());
			Map<String, Date> result = calendarCommon.getCheckinAndCheckoutTime(stopEntity.getStopBegin(), stopEntity.getStopEnd());
			Date checkinTime = result.get("checkinTime");
			Date checkoutTime = result.get("checkoutTime");
			int nightCount = calendarCommon.getNightCount(checkinTime, checkoutTime);
			XbOrderStop orderStop = new XbOrderStop();
			String stopNo = RandomUtil.getStopOrderNo();
			orderStop.setStopNo(stopNo);
			orderStop.setStopStat(AppConstants.OrderStop_stopStat.NEW_0);
			orderStop.setRoomId(stopEntity.getRoomId());
			orderStop.setRoomNo(roomInfo.getRoomName());
			orderStop.setStopBegin(checkinTime);
			orderStop.setStopEnd(checkoutTime);
			orderStop.setActualStopBegin(checkinTime);// 暂时先初始化成一样
			orderStop.setActualStopEnd(checkoutTime);// 暂时先初始化成一样
			orderStop.setContactMobile(stopEntity.getContactMobile());
			orderStop.setContactName(stopEntity.getContactName());
			orderStop.setChainName(roomInfo.getChainName());
			orderStop.setChainId(roomInfo.getChainId());
			orderStop.setCreateDtm(DateUtil.getCurDateTime());
			orderStop.setMemo(stopEntity.getMemo());
			orderStop.setStopDays(nightCount);
			orderStop.setPrice(100);// 发去呼呼的价格,100分
			orderStop.setLastModDtm(DateUtil.getCurDateTime());
			orderStop.setUserId(stopEntity.getUserId());
			orderStop.setUserAcct(stopEntity.getUserAcct());
			Long stopId = (Long) daoUtil.orderStopDao.addAndGetPk(orderStop);
			orderStop.setStopId(stopId.intValue());

			List<CalendarUnit> roomCalList = calendarCommon.queryRoomCalendarByPeriod(stopEntity.getRoomId(), checkinTime, checkoutTime);// 每天的房态和价格
			if (!calendarCommon.checkCanBook(roomCalList)) {
				throw exceptionHandler.newErrorCodeException("-3", "不能下控制单,该时间段已经被预占");
			}
			
			// 修改房态(发送报文)
			roomStateStopBase.roomStateNewStop(stopId.intValue());

			// 要发什么短信吗

			// 记录到log表
			LogStopSheet logStopSheet = new LogStopSheet();
			logStopSheet.setCreateDtm(DateUtil.getCurDateTime());
			logStopSheet.setOperType(AppConstants.LogStopSheet_operType.NEW_1);
			logStopSheet.setStopId(stopId.intValue());
			logStopSheet.setStopNo(stopNo);
			logStopSheet.setUserAcct(stopEntity.getUserAcct());
			logStopSheet.setUserId(stopEntity.getUserId());
			Long sheetId = (Long) daoUtil.logStopSheetDao.addAndGetPk(logStopSheet);
			logStopSheet.setSheetId(sheetId.intValue());
			
			return orderStop;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public void cancelStopSheet(int stopId, int userId, String userAcct) {
		try {
			// 修改xb_order_stop
			XbOrderStop orderStop = daoUtil.orderStopDao.findByPk(stopId);
			if (orderStop.getStopStat() == AppConstants.OrderStop_stopStat.END_2 || orderStop.getStopStat() == AppConstants.OrderStop_stopStat.HAS_BEGAN_CANCEL_3) {
				throw exceptionHandler.newErrorCodeException("-1", "状态错误,不能取消,stop_stat:" + orderStop.getStopStat());
			}
			
			
			XbOrderStop newStop = new XbOrderStop();
			newStop.setStopId(orderStop.getStopId());
			int newStopStat = orderStop.getStopStat() == AppConstants.OrderStop_stopStat.NEW_0 ? AppConstants.OrderStop_stopStat.NOT_BEGIN_CANCEL_4 : AppConstants.OrderStop_stopStat.HAS_BEGAN_CANCEL_3;
			newStop.setStopStat(newStopStat);
			newStop.setActualStopEnd(DateUtil.getCurDateTime());
			daoUtil.orderStopDao.updateEntityByPk(newStop);
			
			// 修改房态(发送报文)
			exceptionHandler.getLog().info("取消停用单,stopId:" + stopId + ",stop_stat:" + orderStop.getStopStat() + ",停用单编号stop_no:" + orderStop.getStopNo());
			if (orderStop.getStopStat() == AppConstants.OrderStop_stopStat.NEW_0) {
				roomStateStopBase.roomStateStopCancel(stopId);
			} else {
				roomStateStopBase.roomStateStopEnd(stopId);
			}

			// 要发什么短信吗

			// 记录到log表
			LogStopSheet logStopSheet = new LogStopSheet();
			logStopSheet.setCreateDtm(DateUtil.getCurDateTime());
			logStopSheet.setOperType(AppConstants.LogStopSheet_operType.CANCEL_2);
			logStopSheet.setStopId(orderStop.getStopId());
			logStopSheet.setStopNo(orderStop.getStopNo());
			logStopSheet.setUserAcct(userAcct);
			logStopSheet.setUserId(userId);
			Long sheetId = (Long) daoUtil.logStopSheetDao.addAndGetPk(logStopSheet);
			logStopSheet.setSheetId(sheetId.intValue());

		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

}
