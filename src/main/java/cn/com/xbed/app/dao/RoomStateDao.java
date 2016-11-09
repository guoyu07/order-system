package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbRoomState;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class RoomStateDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomStateDao.class));

	public boolean add(XbRoomState entity) {
		try {
			return this.insertEntity(entity, "roomStateId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbRoomState entity) {
		try {
			return this.insertEntityAndGetPk(entity, "roomStateId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbRoomState findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbRoomState.class, "select * from xb_room_state where room_state_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbRoomState entity) {
		try {
			if (entity.getRoomStateId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "roomStateId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
