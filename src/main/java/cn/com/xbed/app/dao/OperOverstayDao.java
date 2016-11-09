package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.OperOverstay;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;

@Repository
public class OperOverstayDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OperOverstayDao.class));

	public boolean add(OperOverstay entity) {
		try {
			return this.insertEntity(entity, "operId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(OperOverstay entity) {
		try {
			return this.insertEntityAndGetPk(entity, "operId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public OperOverstay findByPk(int pk) {
		try {
			return this.queryForSingleRow(OperOverstay.class, "select * from oper_overstay where oper_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(OperOverstay entity) {
		try {
			if (entity.getOperId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "operId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	/**
	 * 按照原入住单号查询,取最新的一条
	 * 
	 * @param oriCheckinId
	 * @return
	 */
	public OperOverstay findLatestByOriCheckinId(int oriCheckinId) {
		try {
			String sql = "SELECT * FROM oper_overstay WHERE ori_checkin_id=? ORDER BY create_dtm DESC";
			List<OperOverstay> l = this.queryForMultiRow(OperOverstay.class, sql, new Object[] { oriCheckinId });
			if (!l.isEmpty()) {
				return l.get(0);
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	/**
	 * 根据新的入住单号,取最新的一条记录
	 * 
	 * @param newCheckinId
	 * @return
	 */
	public OperOverstay findLatestByNewCheckinId(int newCheckinId) {
		try {
			String sql = "SELECT * FROM oper_overstay WHERE new_checkin_id=? ORDER BY create_dtm DESC";
			List<OperOverstay> l = this.queryForMultiRow(OperOverstay.class, sql, new Object[] { newCheckinId });
			if (!l.isEmpty()) {
				return l.get(0);
			}
			return null;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
