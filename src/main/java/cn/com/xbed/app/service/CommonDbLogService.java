package cn.com.xbed.app.service;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.LogCleanSheet;
import cn.com.xbed.app.bean.LogCouponCard;
import cn.com.xbed.app.bean.LogEventThrow;
import cn.com.xbed.app.bean.LogOverstayAutoCheckin;
import cn.com.xbed.app.bean.LogPaySucc;
import cn.com.xbed.app.commons.enu.Log_succFlag;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants.CleanSystem;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.EnumHelper;
import cn.com.xbed.app.dao.common.DaoUtil;

@Service
@Transactional
public class CommonDbLogService {

	@Resource
	private DaoUtil daoUtil;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CommonDbLogService.class));

	/**
	 * 发清洁单的时候记录日志 log_clean_sheet
	 * 
	 * @param mobile
	 * @param content
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int logCleanSheet(Integer checkinId, String orderNo, String datagram, int plan, int roomId, Log_succFlag succFlag) {
		try {
			LogCleanSheet cleanSheet = new LogCleanSheet();
			cleanSheet.setCheckinId(checkinId);
			cleanSheet.setCleanSource(CleanSystem.CleanSource.ORDER_SYSTEM_1);
			cleanSheet.setCreateDtm(DateUtil.getCurDateTime());
			cleanSheet.setDatagram(datagram);
			cleanSheet.setOrderNo(orderNo);
			cleanSheet.setPlan(plan);
			cleanSheet.setRoomId(roomId);
			cleanSheet.setSuccFlag(EnumHelper.getEnumString(succFlag));
			return ((Long) daoUtil.cleanSheetDao.addAndGetPk(cleanSheet)).intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 记录事件的日志 log_event_throw
	 * 
	 * @param logTitle
	 * @param logType
	 * @param detailDesc
	 * @param succFlag
	 * @param orderId
	 * @param checkinId
	 * @param stopId
	 * @param eventCode
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int logEvent(String logTitle, int logType, String detailDesc, Log_succFlag succFlag, Integer orderId, Integer checkinId, Integer stopId,
			String eventCode) {
		try {
			LogEventThrow eventThrow = new LogEventThrow();
			eventThrow.setLogTitle(logTitle);
			eventThrow.setLogType(logType);
			eventThrow.setCreateDtm(DateUtil.getCurDateTime());
			eventThrow.setDetailDesc(detailDesc);
			eventThrow.setSuccFlag(EnumHelper.getEnumString(succFlag));
			eventThrow.setOrderId(orderId);
			eventThrow.setCheckinId(checkinId);
			eventThrow.setStopId(stopId);
			eventThrow.setEventCode(eventCode);
			return ((Long) daoUtil.logEventThrowDao.addAndGetPk(eventThrow)).intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 记录log_pay_succ
	 * 
	 * @param logTitle
	 * @param paymentId
	 * @param orderId
	 * @param succFlag
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int logPaySucc(String logTitle, int paymentId, int orderId, Log_succFlag succFlag, String errorMsg) {
		try {
			LogPaySucc logPaySucc = new LogPaySucc();
			logPaySucc.setCreateDtm(DateUtil.getCurDateTime());
			logPaySucc.setLogTitle(logTitle);
			logPaySucc.setOrderId(orderId);
			logPaySucc.setPaymentId(paymentId);
			logPaySucc.setSuccFlag(EnumHelper.getEnumString(succFlag));
			logPaySucc.setErrorMsg(errorMsg);
			return ((Long) daoUtil.logPaySuccDao.addAndGetPk(logPaySucc)).intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int logCouponCard(String logTitle, String orderNo, String cardId, int lodgerId, int lodgerType, Log_succFlag succFlag) {
		try {
			LogCouponCard couponCard = new LogCouponCard();
			couponCard.setCardId(cardId);
			couponCard.setCreateDtm(DateUtil.getCurDateTime());
			couponCard.setLodgerId(lodgerId);
			couponCard.setLogType(lodgerType);
			couponCard.setLogTitle(logTitle);
			couponCard.setOrderNo(orderNo);
			couponCard.setSuccFlag(EnumHelper.getEnumString(succFlag));
			return ((Long) daoUtil.logCouponCardDao.addAndGetPk(couponCard)).intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int logOverstayAutoCheckin(Integer oriCheckinId, Integer newCheckinId, String remark, String errorMsg, Log_succFlag succFlag) {
		try {
			LogOverstayAutoCheckin overstayAutoCheckin = new LogOverstayAutoCheckin();
			overstayAutoCheckin.setCreateDtm(DateUtil.getCurDateTime());
			overstayAutoCheckin.setErrorMsg(errorMsg);
			overstayAutoCheckin.setOriCheckinId(oriCheckinId);
			overstayAutoCheckin.setNewCheckinId(newCheckinId);
			overstayAutoCheckin.setSuccFlag(EnumHelper.getEnumString(succFlag));
			overstayAutoCheckin.setRemark(remark);
			return ((Long) daoUtil.logOverstayAutoCheckinDao.addAndGetPk(overstayAutoCheckin)).intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
