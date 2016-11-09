package cn.com.xbed.app.dao.i;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.IQhhCheckout;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class QhhCheckoutDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(QhhCheckoutDao.class));

	public boolean add(IQhhCheckout entity) {
		try {
			return this.insertEntity(entity, "syncId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(IQhhCheckout entity) {
		try {
			return this.insertEntityAndGetPk(entity, "syncId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public IQhhCheckout findByPk(int pk) {
		try {
			return this.queryForSingleRow(IQhhCheckout.class, "select * from i_qhh_checkout where sync_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(IQhhCheckout entity) {
		try {
			if (entity.getSyncId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "syncId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
