package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbOrderCtrip;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class OrderCtripDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderCtripDao.class));

	public boolean add(XbOrderCtrip entity) {
		try {
			return this.insertEntity(entity, "ctripId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbOrderCtrip entity) {
		try {
			return this.insertEntityAndGetPk(entity, "ctripId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbOrderCtrip findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbOrderCtrip.class, "select * from xb_order_ctrip where ctrip_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbOrderCtrip entity) {
		try {
			if (entity.getCtripId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "ctripId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
