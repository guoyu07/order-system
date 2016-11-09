package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbQunarOrderOper;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class QunarOrderOperDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(QunarOrderOperDao.class));

	public boolean add(XbQunarOrderOper entity) {
		try {
			return this.insertEntity(entity, "operId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbQunarOrderOper entity) {
		try {
			return this.insertEntityAndGetPk(entity, "operId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbQunarOrderOper findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbQunarOrderOper.class, "select * from xb_qunar_order_oper where oper_id=?",
					new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(XbQunarOrderOper entity) {
		try {
			if (entity.getOperId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "operId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbQunarOrderOper findByOrderNoAndStatus(String orderNo, String orderStatus) {
		try {
			return this.queryForSingleRow(XbQunarOrderOper.class,
					"select * from xb_qunar_order_oper where ota_order_no=? AND ota_order_status_code=?",
					new Object[] { orderNo, orderStatus });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public boolean qunarOrderOperExists(String orderNo, String orderStatus) {
		try {
			List<Map<String,Object>> result = this.queryMapList(
					"select oper_id from xb_qunar_order_oper where ota_order_no=? AND ota_order_status_code=?",
					new Object[] { orderNo, orderStatus });
			return result == null || result.isEmpty() ? false : true;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	
	public boolean qunarOrderOperExists(String orderNo) {
		try {
			List<Map<String,Object>> result = this.queryMapList(
					"select oper_id from xb_qunar_order_oper where ota_order_no=?",
					new Object[] { orderNo });
			return result == null || result.isEmpty() ? false : true;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
