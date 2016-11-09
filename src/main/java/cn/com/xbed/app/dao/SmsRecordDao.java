package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbSmsRecord;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class SmsRecordDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SmsRecordDao.class));

	public boolean add(XbSmsRecord entity) {
		try {
			return this.insertEntity(entity, "recordId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbSmsRecord entity) {
		try {
			return this.insertEntityAndGetPk(entity, "recordId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbSmsRecord findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbSmsRecord.class, "select * from xb_sms_record where record_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbSmsRecord entity) {
		try {
			if (entity.getRecordId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "recordId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
