package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbCheckinExt;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class CheckinExtDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CheckinExtDao.class));

	public boolean add(XbCheckinExt entity) {
		try {
			return this.insertEntity(entity, "extId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbCheckinExt entity) {
		try {
			return this.insertEntityAndGetPk(entity, "extId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbCheckinExt findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbCheckinExt.class, "select * from xb_checkin_ext where ext_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbCheckinExt findByCheckinId(int checkinId) {
		try {
			return this.queryForSingleRow(XbCheckinExt.class, "select * from xb_checkin_ext where checkin_id=?", new Object[] { checkinId });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	
	public int updateEntityByPk(XbCheckinExt entity) {
		try {
			if (entity.getExtId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "extId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
