package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbTempRoom;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class TempRoomDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(TempRoomDao.class));

	public boolean add(XbTempRoom entity) {
		try {
			return this.insertEntity(entity, "id", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbTempRoom entity) {
		try {
			return this.insertEntityAndGetPk(entity, "id", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbTempRoom findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbTempRoom.class, "select * from xb_temp_room where id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateEntityByPk(XbTempRoom entity) {
		try {
			if (entity.getId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "id");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
