package cn.com.xbed.app.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.bean.XbCalendarDaily;
import cn.com.xbed.app.dao.basedao.impl.CommonDao;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Repository
public class CalendarDailyDao extends CommonDao {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CalendarDailyDao.class));

	public boolean add(XbCalendarDaily entity) {
		try {
			return this.insertEntity(entity, "calId", true);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	
	public XbCalendarDaily findEntityByRoomIdAndCurDate(int roomId, String curDate) {
		try {
			String sql = "SELECT * FROM xb_calendar_daily WHERE room_id=? AND cur_date=?";
			return this.queryForSingleRow(XbCalendarDaily.class, sql, new Object[]{roomId, curDate});
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	public XbCalendarDaily findLatestByRoomId(int roomId) {
		try {
			List<XbCalendarDaily> list = this.queryForMultiRow(XbCalendarDaily.class,
					"SELECT * FROM xb_calendar_daily WHERE room_id=? ORDER BY cur_date DESC LIMIT 0,1", new Object[] { roomId });
			return list == null || list.size() == 0 ? null : list.get(0);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
	
	
	/**
	 * 
	 * @param roomId
	 * @param months 格式2015-10-27
	 * @return
	 */
	public List<XbCalendarDaily> findByRoomIdAndMonths(int roomId, String months) {
		try {
			String sql = "SELECT * FROM xb_calendar_daily WHERE room_id=? AND cur_month=?";
			List<XbCalendarDaily> list = this.queryForMultiRow(XbCalendarDaily.class, sql, new Object[] { roomId, months });
			return list;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}
}
