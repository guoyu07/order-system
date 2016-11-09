package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Repository
public class CheckinDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CheckinDao.class));

	public boolean add(XbCheckin entity) {
		try {
			return this.insertEntity(entity, "checkinId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public Serializable addAndGetPk(XbCheckin entity) {
		try {
			return this.insertEntityAndGetPk(entity, "checkinId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	// 从主库获取
	public XbCheckin findByPk(int pk) {
		try {
			XbCheckin checkinInfo = this.queryForSingleRow(XbCheckin.class, "select * from xb_checkin where checkin_id=?", new Object[] { pk }, true);
			AssertHelper.notNull(checkinInfo, "查无此入住单,checkinId:" + pk);
			return checkinInfo;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateEntityByPk(XbCheckin entity) {
		try {
			if (entity.getCheckinId() == null) {
				throw new RuntimeException("必须传主键");
			}
			if (entity.getLastModDtm() == null) {
				entity.setLastModDtm(DateUtil.getCurDateTime());
			}
			return this.updateEntityByPk(entity, "checkinId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	// 从主库获取
	public List<XbCheckin> findByOrderId(int orderId) {
		try {
			return this.queryForMultiRow(XbCheckin.class, "select * from xb_checkin where order_id=?", new Object[] { orderId },true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	// 从主库获取
	public XbCheckin findUniqueByOrderId(int orderId) {
		try {
			List<XbCheckin> listCheckin = this.queryForMultiRow(XbCheckin.class, "select * from xb_checkin where order_id=?", new Object[] { orderId }, true);
			if (listCheckin.isEmpty()) {
				return null;
			}
			return listCheckin.get(0);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
}
