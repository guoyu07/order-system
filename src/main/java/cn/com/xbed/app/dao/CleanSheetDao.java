package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.LogCleanSheet;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class CleanSheetDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CleanSheetDao.class));

	public boolean add(LogCleanSheet entity) {
		try {
			return this.insertEntity(entity, "sheetId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(LogCleanSheet entity) {
		try {
			return this.insertEntityAndGetPk(entity, "sheetId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public LogCleanSheet findByPk(int pk) {
		try {
			return this.queryForSingleRow(LogCleanSheet.class, "select * from log_clean_sheet where sheet_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(LogCleanSheet entity) {
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
