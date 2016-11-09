package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class LodgerDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LodgerDao.class));

	public boolean add(XbLodger entity) {
		try {
			return this.insertEntity(entity, "lodgerId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbLodger entity) {
		try {
			return this.insertEntityAndGetPk(entity, "lodgerId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	// 从主库获取
	public XbLodger findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbLodger.class, "select * from xb_lodger where lodger_id=?",
					new Object[] { pk },true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(XbLodger entity) {
		try {
			if (entity.getLodgerId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "lodgerId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	// 从主库获取
	public XbLodger findByMobile(String mobile) {
		try {
			return this.queryForSingleRow(XbLodger.class, "select * from xb_lodger where mobile=?",
					new Object[] { mobile },true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
