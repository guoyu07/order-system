package cn.com.xbed.app.service;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.SvSysParam;
import cn.com.xbed.app.bean.XbSmsRecord;
import cn.com.xbed.app.bean.XbSmsTemplate;
import cn.com.xbed.app.bean.XbSmsValid;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.smsmodule.CommonSms;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.RandomUtil;

@Service
@Transactional
public class ValidService {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonSms commonSms;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ValidService.class));

	public int sendRandomSms(String mobile) {
		try {
			Date now = DateUtil.getCurDateTime();
			String random = RandomUtil.getRandomStr(6);
			XbSmsTemplate smsTemplateConf = daoUtil.smsTemplateDao.getByParamKey("RANDOM_NUMBER_SMS");
			String smsTemplate = smsTemplateConf.getSmsContent();
			String smsContent = smsTemplate.replaceAll("<RANDOM>", random);
			String timeout = "60";
			SvSysParam timeoutSysParam = daoUtil.sysParamDao.getByParamKey("RANDOM_SMS_TIMEOUT_PERIOD");
			if (timeoutSysParam != null && timeoutSysParam.getParamValue() != null
					&& !timeoutSysParam.getParamValue().equals("")) {
				timeout = timeoutSysParam.getParamValue();
			}
			smsContent = smsContent.replaceAll("<TIMEOUT>", timeout);
			// 写入验证表
			XbSmsValid valid = new XbSmsValid();
			valid.setMobile(mobile);
			valid.setRandomNo(random);
			valid.setSendDtm(now);
			valid.setExpireDtm(DateUtil.addSecounds(now, Integer.parseInt(timeout)));
			Long validPk = (Long) daoUtil.smsValidDao.addAndGetPk(valid);

			// 写入短信表
			XbSmsRecord record = new XbSmsRecord();
			record.setCreateTime(now);
			record.setMessage(smsContent);
			record.setTelno(mobile);
			record.setTplId(smsTemplateConf.getTplId());
			daoUtil.smsRecordDao.add(record);
			return validPk.intValue();
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// -1查不到需要验证的 -2超时 -3验证错误 1正确
	public void validRandom(int validId, String validVal) {
		try {
			Date now = DateUtil.getCurDateTime();
			XbSmsValid valid = daoUtil.smsValidDao.findByPk(validId);
			if (valid == null) {
				throw exceptionHandler.newErrorCodeException("-1", "该validId未发送任何验证码validId[" + validId + "]");
			}
			// 检查超时
			if (DateUtil.compareDateTime(now, valid.getExpireDtm()) > 0) {
				throw exceptionHandler.newErrorCodeException("-2", "验证超时");
			}

			// 校验验证码
			if (validVal == null || !validVal.equals(valid.getRandomNo())) {
				throw exceptionHandler.newErrorCodeException("-3", "验证错误");
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public XbSmsValid findSmsValidByValidId(Integer validId) {
		
		return daoUtil.smsValidDao.findByPk(validId);
		
	}
}
