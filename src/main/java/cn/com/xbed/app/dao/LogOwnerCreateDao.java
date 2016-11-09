package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.LogOwnerCreate;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class LogOwnerCreateDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LogOwnerCreateDao.class));

	public boolean add(LogOwnerCreate entity) {
		try {
			return this.insertEntity(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(LogOwnerCreate entity) {
		try {
			return this.insertEntityAndGetPk(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public LogOwnerCreate findByPk(int pk) {
		try {
			return this.queryForSingleRow(LogOwnerCreate.class, "select * from log_owner_create where log_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(LogOwnerCreate entity) {
		try {
			if (entity.getLogId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "logId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
