package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbCleanRec;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class CleanRecDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CleanRecDao.class));

	public boolean add(XbCleanRec entity) {
		try {
			return this.insertEntity(entity, "applyId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbCleanRec entity) {
		try {
			return this.insertEntityAndGetPk(entity, "applyId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbCleanRec findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbCleanRec.class, "select * from xb_clean_apply where apply_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbCleanRec entity) {
		try {
			if (entity.getApplyId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "applyId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int changeHandleSts(Byte handleSts, Integer orderId) {
		try {
			return this.updateOrDelete("update xb_clean_rec set handle_sts=? where order_id=?",
					new Object[] { handleSts, orderId });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
