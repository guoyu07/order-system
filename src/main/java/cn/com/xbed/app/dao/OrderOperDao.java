package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbOrderOper;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class OrderOperDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OrderOperDao.class));

	public boolean add(XbOrderOper entity) {
		try {
			return this.insertEntity(entity, "operId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbOrderOper entity) {
		try {
			return this.insertEntityAndGetPk(entity, "operId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbOrderOper findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbOrderOper.class, "select * from xb_order_oper where oper_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbOrderOper entity) {
		try {
			if (entity.getOperId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "operId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
