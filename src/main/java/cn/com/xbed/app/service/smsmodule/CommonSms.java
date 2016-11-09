package cn.com.xbed.app.service.smsmodule;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbSmsRecord;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
@Transactional
public class CommonSms {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private CommonService commonService;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CommonSms.class));

	// 发送短信
	public int sendSms(String mobile, String content) {
		try {
			XbSmsRecord record = new XbSmsRecord();
			record.setCreateTime(DateUtil.getCurDateTime());
			record.setMessage(content);
			record.setTelno(mobile);
			record.setTplId(-1);
			Long lon = (Long) daoUtil.smsRecordDao.addAndGetPk(record);
			return lon.intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public int addSmsRecord(String smsContent, String mobile, int tplId) {
		try {
			XbSmsRecord record = new XbSmsRecord();
			record.setCreateTime(DateUtil.getCurDateTime());
			record.setMessage(smsContent);
			record.setTelno(mobile);
			record.setTplId(tplId);
			Long smsId = (Long) daoUtil.smsRecordDao.addAndGetPk(record);
			return smsId.intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
