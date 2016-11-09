package cn.com.xbed.app.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbSchedule;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Repository
public class ScheduleDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ScheduleDao.class));

	public boolean add(XbSchedule entity) {
		try {
			return this.insertEntity(entity, "scheduleId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	public XbSchedule findByPk(int pk) {
		try {
			return this.queryForSingleRow(XbSchedule.class, "select * from xb_schedule where schedule_id=?", new Object[] { pk });
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public int updateRoomByPk(XbSchedule entity) {
		try {
			if (entity.getScheduleId() == null) {
				throw new RuntimeException("必须传主键");
			}
			return this.updateEntityByPk(entity, "roomId");
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	/**
	 * 查询某个月份的计划
	 * @param roomId
	 * @param date date为null则忽略条件
	 * @return
	 */
	public List<XbSchedule> findByRoomId(int roomId, Date date) {
		try {
			String sql = "SELECT * FROM xb_schedule WHERE room_id=? AND sch_date>=? AND sch_date<=? ORDER BY sch_date ASC";
			List<Object> param = new ArrayList<>();
			param.add(roomId);
			param.add(DateUtil.getStartDayOfMonth(date));
			param.add(DateUtil.getLastDayOfMonth(date));
			return this.queryForMultiRow(XbSchedule.class, sql, param.toArray());
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
