package cn.com.xbed.app.dao;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbPromoRoomGroup;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class PromoRoomGroupDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(PromoRoomGroupDao.class));

	public boolean add(XbPromoRoomGroup entity) {
		try {
			return this.insertEntity(entity, "roomGroupId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbPromoRoomGroup entity) {
		try {
			return this.insertEntityAndGetPk(entity, "roomGroupId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbPromoRoomGroup findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbPromoRoomGroup.class, "select * from xb_promo_room_group where room_group_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbPromoRoomGroup findAll() {
		try {
			return this.queryForSingleRow(XbPromoRoomGroup.class, "select * from xb_promo_room_group");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbPromoRoomGroup entity) {
		try {
			if (entity.getRoomGroupId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "roomGroupId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
