package cn.com.xbed.app.dao;

import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbFacility;
import cn.com.xbed.app.bean.XbRoomFacilityRel;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Repository
public class RoomFacilityRelDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomFacilityRelDao.class));

	public boolean add(XbRoomFacilityRel entity) {
		try {
			return this.insertEntity(entity, "relId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	
	public int delByRoomIdAndFaciId(int roomId, int facilityId) {
		try {
			return this.updateOrDelete("DELETE FROM xb_room_facility_rel WHERE room_id=? AND facility_id=?", new Object[]{roomId, facilityId});
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbRoomFacilityRel findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbRoomFacilityRel.class, "select * from xb_room_facility_rel where rel_id=?",
					new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateRoomByPk(XbRoomFacilityRel entity) {
		try {
			AssertHelper.notNull(entity.getRelId());
			return this.updateEntityByPk(entity, "relId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public List<XbRoomFacilityRel> findValidByRoomId(int roomId, int facilityType) {
		try {
			return this.queryForMultiRow(XbRoomFacilityRel.class,
					"SELECT a.* FROM xb_room_facility_rel a,xb_facility b WHERE a.facility_id=b.facility_id AND a.room_id=? AND b.flag=? AND b.facility_type=?",
					new Object[] { roomId, AppConstants.Facility_flag.NORMAL_1, facilityType });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	
	
	public List<XbFacility> findValidFacilityByRoomId(int roomId, int facilityType) {
		try {
			String sql = "SELECT f.* FROM xb_room_facility_rel r,xb_facility f WHERE r.facility_id=f.facility_id AND f.flag=? AND r.room_id=? AND facility_type=?";
			return this.queryForMultiRow(XbFacility.class, sql, new Object[]{AppConstants.Room_flag.NORMAL_1, roomId, facilityType});
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
}
