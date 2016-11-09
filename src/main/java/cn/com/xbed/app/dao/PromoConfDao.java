package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbPromoConf;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class PromoConfDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(PromoConfDao.class));

	public boolean add(XbPromoConf entity) {
		try {
			return this.insertEntity(entity, "promoConfId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbPromoConf entity) {
		try {
			return this.insertEntityAndGetPk(entity, "promoConfId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbPromoConf findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbPromoConf.class, "select * from xb_promo_conf where promo_conf_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbPromoConf entity) {
		try {
			if (entity.getPromoConfId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "promoConfId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
