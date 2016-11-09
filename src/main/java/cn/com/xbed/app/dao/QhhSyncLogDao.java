package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbQhhSyncLog;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class QhhSyncLogDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(QhhSyncLogDao.class));

	public boolean add(XbQhhSyncLog entity) {
		try {
			return this.insertEntity(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbQhhSyncLog entity) {
		try {
			return this.insertEntityAndGetPk(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbQhhSyncLog findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbQhhSyncLog.class, "select * from xb_qhh_sync_log where log_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(XbQhhSyncLog entity) {
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
