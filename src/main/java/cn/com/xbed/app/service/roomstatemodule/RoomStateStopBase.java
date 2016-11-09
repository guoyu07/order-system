package cn.com.xbed.app.service.roomstatemodule;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbOrderStop;
import cn.com.xbed.app.commons.enu.OperCodeStop;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Service
@Transactional
public class RoomStateStopBase {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private SendStopDatagramTool sendStopDatagramTool;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomStateStopBase.class));

	public void roomStateNewStop(int stopId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】新建停用,stopId:" + stopId);
			XbOrderStop stop = daoUtil.orderStopDao.findByPk(stopId);
			String datagram = sendStopDatagramTool.getNewStopDatagram(stopId);
			calendarCommon.operStop(stop.getRoomId(), stop.getStopBegin(), stop.getStopEnd(), OperCodeStop.NEW_STOP, datagram);
			exceptionHandler.getLog().info("$$end【房态接口】新建停用,stopId:" + stopId);
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException(e);
		}
	}

	public void roomStateStopBegin(int stopId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】停用开始,stopId:" + stopId);
			XbOrderStop orderStop = daoUtil.orderStopDao.findByPk(stopId);
			String datagram = sendStopDatagramTool.getStopBeginDatagram(stopId);
			calendarCommon.operStop(orderStop.getRoomId(), orderStop.getStopBegin(), orderStop.getStopEnd(), OperCodeStop.STOP_BEGIN, datagram);
			exceptionHandler.getLog().info("$$end【房态接口】停用开始,stopId:" + stopId);
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException(e);
		}
	}

	public void roomStateStopEnd(int stopId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】停用结束,stopId:" + stopId);
			XbOrderStop orderStop = daoUtil.orderStopDao.findByPk(stopId);
			String datagram = sendStopDatagramTool.getStopEndDatagram(stopId);
			calendarCommon.operStop(orderStop.getRoomId(), orderStop.getStopBegin(), orderStop.getStopEnd(), OperCodeStop.STOP_END, datagram);
			exceptionHandler.getLog().info("$$end【房态接口】停用结束,stopId:" + stopId);
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException(e);
		}
	}

	public void roomStateStopCancel(int stopId) {
		try {
			exceptionHandler.getLog().info("$$start【房态接口】停用取消,stopId:" + stopId);
			XbOrderStop orderStop = daoUtil.orderStopDao.findByPk(stopId);
			String datagram = sendStopDatagramTool.getStopCancelDatagram(stopId);
			calendarCommon.operStop(orderStop.getRoomId(), orderStop.getStopBegin(), orderStop.getStopEnd(), OperCodeStop.STOP_CANCEL, datagram);
			exceptionHandler.getLog().info("$$end【房态接口】停用取消,stopId:" + stopId);
		} catch (Exception e) {
			// 有没有必要记录下来?
			throw exceptionHandler.logServiceException(e);
		}
	}
}
