package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbRoomCtrlSheet;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Repository
public class RoomCtrlSheetDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomCtrlSheetDao.class));

	public boolean add(XbRoomCtrlSheet entity) {
		try {
			return this.insertEntity(entity, "ctrlId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbRoomCtrlSheet entity) {
		try {
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.insertEntityAndGetPk(entity, "ctrlId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbRoomCtrlSheet findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbRoomCtrlSheet.class, "select * from xb_room_ctrl_sheet where ctrl_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbRoomCtrlSheet entity) {
		try {
			if (entity.getCtrlId() == null) {
				throw new RuntimeException("必须传主键");
			}
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.updateEntityByPk(entity, "ctrlId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
