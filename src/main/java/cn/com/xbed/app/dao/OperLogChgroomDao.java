package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbOperLogChgroom;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Repository
public class OperLogChgroomDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OperLogChgroomDao.class));

	public boolean add(XbOperLogChgroom entity) {
		try {
			return this.insertEntity(entity, "operId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbOperLogChgroom entity) {
		try {
			return this.insertEntityAndGetPk(entity, "operId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbOperLogChgroom findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbOperLogChgroom.class, "select * from xb_oper_log_chgroom where oper_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbOperLogChgroom entity) {
		try {
			if (entity.getOperId() == null) {
				throw new RuntimeException("必须传主键");
			}
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.updateEntityByPk(entity, "operId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
