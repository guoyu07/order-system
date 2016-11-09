package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbManSendsmsRec;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class ManSendsmsDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ManSendsmsDao.class));

	public boolean add(XbManSendsmsRec entity) {
		try {
			return this.insertEntity(entity, "recId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbManSendsmsRec entity) {
		try {
			return this.insertEntityAndGetPk(entity, "recId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbManSendsmsRec findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbManSendsmsRec.class, "select * from xb_man_sendsms_rec where rec_id=?",
					new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(XbManSendsmsRec entity) {
		try {
			if (entity.getRecId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "recId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
