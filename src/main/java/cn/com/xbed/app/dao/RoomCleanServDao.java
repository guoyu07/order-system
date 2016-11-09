package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbRoomCleanServ;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class RoomCleanServDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomCleanServDao.class));

	public boolean add(XbRoomCleanServ entity) {
		try {
			return this.insertEntity(entity, "cleanServId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbRoomCleanServ entity) {
		try {
			return this.insertEntityAndGetPk(entity, "cleanServId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbRoomCleanServ findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbRoomCleanServ.class, "select * from xb_room_clean_serv where clean_serv_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbRoomCleanServ findByRoomId(int roomId) {
		try {
			return this.queryForSingleRow(XbRoomCleanServ.class, "select * from xb_room_clean_serv where room_id=?", new Object[] { roomId });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbRoomCleanServ entity) {
		try {
			if (entity.getCleanServId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "cleanServId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
