package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbOrderStop;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Repository
public class OrderStopDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderStopDao.class));

	public boolean add(XbOrderStop entity) {
		try {
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.insertEntity(entity, "stopId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbOrderStop entity) {
		try {
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.insertEntityAndGetPk(entity, "stopId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbOrderStop findByPk(int pk) {
		try {
			XbOrderStop orderStop = this.queryForSingleRow(XbOrderStop.class, "select * from xb_order_stop where stop_id=?", new Object[] { pk }, true);
			AssertHelper.notNull(orderStop, "查无此停用单,stopId:" + pk);
			return orderStop;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbOrderStop entity) {
		try {
			if (entity.getStopId() == null) {
				throw new RuntimeException("必须传主键");
			}
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.updateEntityByPk(entity, "stopId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
