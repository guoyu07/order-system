package cn.com.xbed.app.dao.i;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.IFromQhh;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class FromQhhDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(FromQhhDao.class));

	public boolean add(IFromQhh entity) {
		try {
			return this.insertEntity(entity, "jobId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(IFromQhh entity) {
		try {
			return this.insertEntityAndGetPk(entity, "jobId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public IFromQhh findByPk(int pk) {
		try {
			return this.queryForSingleRow(IFromQhh.class, "select * from i_from_qhh where job_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(IFromQhh entity) {
		try {
			if (entity.getJobId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "jobId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
