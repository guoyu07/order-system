package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.CmLocation;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class LocationDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LocationDao.class));

	public boolean add(CmLocation entity) {
		try {
			return this.insertEntity(entity, "locId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(CmLocation entity) {
		try {
			return this.insertEntityAndGetPk(entity, "locId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public CmLocation findByPk(int pk) {
		try {
			return this.queryForSingleRow(CmLocation.class, "select * from cm_location where loc_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(CmLocation entity) {
		try {
			if (entity.getLocId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "locId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
