package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbOrderPromo;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class OrderPromoDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderPromoDao.class));

	public boolean add(XbOrderPromo entity) {
		try {
			return this.insertEntity(entity, "promoId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbOrderPromo entity) {
		try {
			return this.insertEntityAndGetPk(entity, "promoId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbOrderPromo findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbOrderPromo.class, "select * from xb_order_promo where promo_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbOrderPromo entity) {
		try {
			if (entity.getPromoId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "promoId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
