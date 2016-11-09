package cn.com.xbed.app.service;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbOperLogChgroom;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
@Transactional
public class OperLogCommon {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private OmsUserService omsUserService;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OperLogCommon.class));
	
		
	// -1数据问题,-2开门密码为空数据问题
	public Serializable saveOperLogChgroom(String account, int oriOrderId, int oriCheckinId, int newOrderId, int newCheckinId, String memo) {
		try {
			int userId = omsUserService.queryOmsUserId(account);
			XbOperLogChgroom chgroom = new XbOperLogChgroom();
			chgroom.setOriOrderId(oriOrderId);
			chgroom.setOriCheckinId(oriCheckinId);
			chgroom.setNewOrderId(newOrderId);
			chgroom.setNewCheckinId(newCheckinId);
			chgroom.setChgroomDtm(DateUtil.getCurDateTime());
			chgroom.setUserId(userId);
			chgroom.setMemo(memo);
			return daoUtil.operLogChgroomDao.addAndGetPk(chgroom);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public Serializable saveOperLogChgroom(int userId, int oriOrderId, int oriCheckinId, int newOrderId, int newCheckinId, String memo) {
		try {
			XbOperLogChgroom chgroom = new XbOperLogChgroom();
			chgroom.setOriOrderId(oriOrderId);
			chgroom.setOriCheckinId(oriCheckinId);
			chgroom.setNewOrderId(newOrderId);
			chgroom.setNewCheckinId(newCheckinId);
			chgroom.setChgroomDtm(DateUtil.getCurDateTime());
			chgroom.setUserId(userId);
			chgroom.setMemo(memo);
			return daoUtil.operLogChgroomDao.addAndGetPk(chgroom);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
