package cn.com.xbed.app.service.logmodule;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.LogOwnerCreate;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Service
@Transactional
public class LogModule {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LogModule.class));
	@Resource
	private DaoUtil daoUtil;

	// 早餐系统发送短信并记录下发送记录
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public int logOwnerCreate(LogOwnerCreate logOwnerCreate) {
		try {
			return ((Long) daoUtil.logOwnerCreateDao.addAndGetPk(logOwnerCreate)).intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	

}
