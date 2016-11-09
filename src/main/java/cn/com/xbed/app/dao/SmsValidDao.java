package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbSmsValid;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class SmsValidDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SmsValidDao.class));

	public boolean add(XbSmsValid entity) {
		try {
			return this.insertEntity(entity, "validId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbSmsValid entity) {
		try {
			return this.insertEntityAndGetPk(entity, "validId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbSmsValid findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbSmsValid.class, "select * from xb_sms_valid where valid_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbSmsValid entity) {
		try {
			if (entity.getValidId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "validId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
