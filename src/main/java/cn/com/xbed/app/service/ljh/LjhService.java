package cn.com.xbed.app.service.ljh;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.smsmodule.CommonSms;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Service
@Transactional
public class LjhService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LjhService.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonSms commonSms;

	
	// 早餐系统发送短信并记录下发送记录
	public int sendSms(String mobile, String content) {
		try {
			return commonSms.sendSms(mobile, content);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
