package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.LogPaySucc;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;

@Repository
public class LogPaySuccDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LogPaySuccDao.class));

	public boolean add(LogPaySucc entity) {
		try {
			return this.insertEntity(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(LogPaySucc entity) {
		try {
			return this.insertEntityAndGetPk(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public LogPaySucc findByPk(int pk) {
		try {
			return this.queryForSingleRow(LogPaySucc.class, "select * from log_pay_succ where log_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(LogPaySucc entity) {
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
