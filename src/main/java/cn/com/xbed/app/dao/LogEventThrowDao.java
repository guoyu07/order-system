package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.LogEventThrow;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class LogEventThrowDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LogEventThrow.class));

	public boolean add(LogEventThrow entity) {
		try {
			return this.insertEntity(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(LogEventThrow entity) {
		try {
			return this.insertEntityAndGetPk(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public LogEventThrow findByPk(int pk) {
		try {
			return this.queryForSingleRow(LogEventThrow.class, "select * from log_event_throw where log_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(LogEventThrow entity) {
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
