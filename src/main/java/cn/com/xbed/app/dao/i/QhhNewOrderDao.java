package cn.com.xbed.app.dao.i;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.IQhhNewOrder;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class QhhNewOrderDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(QhhNewOrderDao.class));

	public boolean add(IQhhNewOrder entity) {
		try {
			return this.insertEntity(entity, "syncId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(IQhhNewOrder entity) {
		try {
			return this.insertEntityAndGetPk(entity, "syncId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public IQhhNewOrder findByPk(int pk) {
		try {
			return this.queryForSingleRow(IQhhNewOrder.class, "select * from i_qhh_new_order where sync_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(IQhhNewOrder entity) {
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
