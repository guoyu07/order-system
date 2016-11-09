package cn.com.xbed.app.service.locksystem;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.smsmodule.CommonSms;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Service
@Transactional
public class LockSystemService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LockSystemService.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonSms commonSms;

	//给门锁系统调用的接口(KIRA)
	public int sendSms(String mobile, String content) {
		try {
			return commonSms.sendSms(mobile, content);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
