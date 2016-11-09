package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.LogCouponCard;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;

@Repository
public class LogCouponCardDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LogCouponCardDao.class));

	public boolean add(LogCouponCard entity) {
		try {
			return this.insertEntity(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(LogCouponCard entity) {
		try {
			return this.insertEntityAndGetPk(entity, "logId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public LogCouponCard findByPk(int pk) {
		try {
			return this.queryForSingleRow(LogCouponCard.class, "select * from log_coupon_card where log_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(LogCouponCard entity) {
		try {
			if (entity.getLodgerId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "logId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
