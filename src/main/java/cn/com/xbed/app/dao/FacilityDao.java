package cn.com.xbed.app.dao;

import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbFacility;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;


@Repository
public class FacilityDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(FacilityDao.class));

	public boolean add(XbFacility entity) {
		try {
			return this.insertEntity(entity, "facilityId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbFacility findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbFacility.class, "select * from xb_facility where facility_id=?",
					new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public int updateRoomByPk(XbFacility entity) {
		try {
			if (entity.getFacilityId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "facilityId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public List<XbFacility> findValidByFacilityType(int facilityType) {
		try {
			return this.queryForMultiRow(XbFacility.class, "select * from xb_facility where flag=? and facility_type=?",
					new Object[] { AppConstants.Facility_flag.NORMAL_1, facilityType });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

}
