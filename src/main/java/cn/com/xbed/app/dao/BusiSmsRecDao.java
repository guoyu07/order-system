package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbBusiSmsRec;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class BusiSmsRecDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(BusiSmsRecDao.class));

	public boolean add(XbBusiSmsRec entity) {
		try {
			return this.insertEntity(entity, "recId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbBusiSmsRec entity) {
		try {
			return this.insertEntityAndGetPk(entity, "recId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbBusiSmsRec findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbBusiSmsRec.class, "select * from xb_busi_sms_rec where rec_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbBusiSmsRec entity) {
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
