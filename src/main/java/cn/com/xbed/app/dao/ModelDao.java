package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbCalendarDaily;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Repository
public class ModelDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ModelDao.class));

	public boolean add(XbCalendarDaily entity) {
		try {
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.insertEntity(entity, "calId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbCalendarDaily entity) {
		try {
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.insertEntityAndGetPk(entity, "calId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbCalendarDaily findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbCalendarDaily.class, "select * from xb_calendar_daily where cal_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbCalendarDaily entity) {
		try {
			if (entity.getCalId() == null) {
				throw new RuntimeException("必须传主键");
			}
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.updateEntityByPk(entity, "calId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
