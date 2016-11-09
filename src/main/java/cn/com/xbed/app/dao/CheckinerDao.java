package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class CheckinerDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CheckinerDao.class));

	public boolean add(XbCheckiner entity) {
		try {
			return this.insertEntity(entity, "checkinerId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public Serializable addAndGetPk(XbCheckiner entity) {
		try {
			return this.insertEntityAndGetPk(entity, "checkinerId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	public List<Map<String, Object>> findBycheckinIdMini(Integer checkInId) {
		return this.queryMapList("select checkiner_id,lodger_id,name from xb_checkiner where checkin_id=?", new Object[]{ checkInId });
	}
	
	public List<Map<String, Object>> findBycheckInId(Integer checkInId) {
		return this.queryMapList("select c.lodger_id,c.name,c.mobile,l.id_card,l.card_front,c.is_main from xb_checkiner c left join xb_lodger l on c.lodger_id=l.lodger_id  where c.is_del=0 and c.is_main >= 0 and c.checkin_id=?", new Object[]{ checkInId }, true);
	}
	
	public List<Map<String, Object>> findByOrderId(Integer orderId) {
		return this.queryMapList("select lodger_id lodgerId,name,checkin.checkin_id checkinId,xb_checkiner.mobile from (select checkin_id from xb_checkin where order_id=?) checkin,xb_checkiner where checkin.checkin_id=xb_checkiner.checkin_id and xb_checkiner.is_main >= 0", new Object[]{ orderId }, true);
	}
	public XbCheckiner findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbCheckiner.class, "select * from xb_checkiner where checkiner_id=?", new Object[] { pk }, true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<XbCheckiner> findByCheckinIdAndByMobile(int checkinId, String mobile) {
		try {
			return this.queryForMultiRow(XbCheckiner.class, "select * from xb_checkiner where checkin_id=? and mobile=? and is_main >= 0", new Object[] { checkinId, mobile }, true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<XbCheckiner> findByCheckinId(int checkinId) {
		try {
			return this.queryForMultiRow(XbCheckiner.class, "select * from xb_checkiner where checkin_id=?", new Object[] { checkinId }, true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<XbCheckiner> findValidByCheckinId(int checkinId) {
		try {
			return this.queryForMultiRow(XbCheckiner.class, "select * from xb_checkiner where checkin_id=? and is_del=0", new Object[] { checkinId });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public List<XbCheckiner> findByCheckinIdAndByNotMobile(int checkinId, List<String> mobiles){
		try {
			StringBuffer sql = new StringBuffer("select * from xb_checkiner where checkin_id=?");
			List<Object> wheres = new ArrayList<Object>();
			wheres.add(checkinId);
			if(mobiles != null && mobiles.size() > 0){
				sql.append(" and mobile not in (");
				for(int i=0;i<mobiles.size();i++){
					sql.append("?,");
				}
				sql = new StringBuffer(sql.substring(0, sql.length() - 1) + ")");
				wheres.addAll(mobiles);
			}
			return this.queryForMultiRow(XbCheckiner.class, sql.toString(), wheres.toArray());
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	
	public int updateEntityByPk(XbCheckiner entity) {
		try {
			if (entity.getCheckinerId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "checkinerId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
