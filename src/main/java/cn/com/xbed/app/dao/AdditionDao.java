package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbAddition;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class AdditionDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(AdditionDao.class));

	public boolean add(XbAddition entity) {
		try {
			return this.insertEntity(entity, "addId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public Serializable addAndGetPk(XbAddition entity) {
		try {
			return this.insertEntityAndGetPk(entity, "addId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbAddition findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbAddition.class, "select * from xb_addition where add_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateRoomByPk(XbAddition entity) {
		try {
			if (entity.getAddId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "addId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<XbAddition> findByRoomId(int roomId) {
		try {
			return this.queryForMultiRow(XbAddition.class, "select * from xb_addition where room_id=?", new Object[] { roomId });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
