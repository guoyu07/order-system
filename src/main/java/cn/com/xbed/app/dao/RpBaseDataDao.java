package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.RpBaseData;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Repository
public class RpBaseDataDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RpBaseDataDao.class));

	public boolean add(RpBaseData entity) {
		try {
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.insertEntity(entity, "baseId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(RpBaseData entity) {
		try {
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.insertEntityAndGetPk(entity, "baseId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public RpBaseData findByPk(int pk) {
		try {
			return this.queryForSingleRow(RpBaseData.class, "select * from rp_base_data where base_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(RpBaseData entity) {
		try {
			if (entity.getBaseId() == null) {
				throw new RuntimeException("必须传主键");
			}
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.updateEntityByPk(entity, "baseId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
