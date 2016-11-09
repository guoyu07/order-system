package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.LogStopSheet;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class LogStopSheetDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LogStopSheetDao.class));

	public boolean add(LogStopSheet entity) {
		try {
			return this.insertEntity(entity, "sheetId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(LogStopSheet entity) {
		try {
			return this.insertEntityAndGetPk(entity, "sheetId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public LogStopSheet findByPk(int pk) {
		try {
			return this.queryForSingleRow(LogStopSheet.class, "select * from log_stop_sheet where sheet_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(LogStopSheet entity) {
		try {
			if (entity.getSheetId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "sheetId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
