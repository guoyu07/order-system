package cn.com.xbed.app.dao;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbSmsTemplate;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class SmsTemplateDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SmsTemplateDao.class));
	

	public XbSmsTemplate getByParamKey(String tplKey) {
		try {
			XbSmsTemplate smsTemplate = this.queryForSingleRow(XbSmsTemplate.class, "select * from xb_sms_template where tpl_key=?", new Object[] { tplKey });
			if (smsTemplate == null) {
				throw new RuntimeException("未配置短信模板: [" + tplKey + "]");
			}
			return smsTemplate;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	/**
	 * 通过key值查找系统参数值，未配置或配置无效(空串)都将抛出运行时异常
	 * @param tplKey
	 * @return
	 */
	public String getValueByParamKey(String tplKey) {
		try {
			XbSmsTemplate smsTemplate = this.getByParamKey(tplKey);
			if (smsTemplate == null || smsTemplate.getSmsContent() == null || smsTemplate.getSmsContent().trim().equals("")) {
				throw new RuntimeException("未配置短信模板: [" + tplKey + "]");
			}
			return smsTemplate.getSmsContent();
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
