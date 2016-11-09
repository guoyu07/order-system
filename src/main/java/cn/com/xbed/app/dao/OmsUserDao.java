package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.OmsUser;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class OmsUserDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OmsUserDao.class));

	public boolean add(OmsUser entity) {
		try {
			return this.insertEntity(entity, "roomId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(OmsUser entity) {
		try {
			return this.insertEntityAndGetPk(entity, "roomId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public OmsUser findByPk(int pk) {
		try {
			return this.queryForSingleRow(OmsUser.class, "select * from oms_user where user_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(OmsUser entity) {
		try {
			if (entity.getUserId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "userId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
