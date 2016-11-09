package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbOrderExt;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class OrderExtDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderExtDao.class));

	public boolean add(XbOrderExt entity) {
		try {
			return this.insertEntity(entity, "extId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbOrderExt entity) {
		try {
			return this.insertEntityAndGetPk(entity, "extId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbOrderExt findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbOrderExt.class, "select * from xb_order_ext where ext_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbOrderExt findByOrderId(int orderId) {
		try {
			XbOrderExt orderExt =  this.queryForSingleRow(XbOrderExt.class, "select * from xb_order_ext where order_id=?", new Object[] { orderId });
			if (orderExt == null) {
				XbOrderExt newOrderExt = new XbOrderExt();
				newOrderExt.setOrderId(orderId);
				this.add(newOrderExt);
			}
			return orderExt;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbOrderExt entity) {
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
