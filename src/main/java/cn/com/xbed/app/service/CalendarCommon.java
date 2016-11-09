package cn.com.xbed.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbCalendarDaily;
import cn.com.xbed.app.bean.vo.CalendarUnit;
import cn.com.xbed.app.bean.vo.RemoteCalendarUnit;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.AppConstants.CalendarDaily_stat;
import cn.com.xbed.app.commons.enu.OperCodeCheckin;
import cn.com.xbed.app.commons.enu.OperCodeFix;
import cn.com.xbed.app.commons.enu.OperCodeOrder;
import cn.com.xbed.app.commons.enu.OperCodeStop;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.roomstatemodule.RoomStateTool;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;

@Service
@Transactional
public class CalendarCommon {
	@Resource
	private DaoUtil daoUtil;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(CalendarCommon.class));
	@Resource
	private RoomStateTool roomStateTool;

	/**
	 * 获得房晚数(必须考虑2015-11-17 9:00到2015-11-17 12:00应该算1个房晚) <br>
	 * 查询两个时间之间相差的天数,不足一天按一天计算
	 * 
	 * @param checkinTime 入住时间,xb_checkin.checkin_time
	 * @param checkoutTime 离店时间,,xb_checkin.checkout_time
	 */
	public int getNightCount(Date checkinTime, Date checkoutTime) {
		try{
			return (int) DateUtil.getTwoDateTimeInterval(checkoutTime, checkinTime);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	/**
	 * 根据传入的具体时间,返回具体要锁定的天数,例如2015-11-10 10:25:00 ~ 2015-11-11
	 * 16:11:00会返回,2015-11-09 00:00:00~2015-11-12 00:00:00 <br>
	 * 当天12:00:00前(包含)
	 * 
	 * 去呼呼的逻辑(去呼呼只能精确到小时):<br>
	 * 1）如起止时间为2015-9-4 11:00至2015-9-4 12:00，占了2015-9-3房态<br>
	 * 2）如起止时间为2015-9-4 12:00至2015-9-4 14:00，占了2015-9-4房态<br>
	 * 3）如起止时间为2015-9-3 12:00至2015-9-5 14:00，占了2015-9-3/2015-9-4/2015-9-5房态<br>
	 * 4）如起止时间为2015-9-5 12:00至2015-9-7 12:00，占了2015-9-5/2015-9-6房态<br>
	 * 
	 * @param checkinTime
	 * @param checkoutTime
	 * @return 返回的beginDate和endDate都只有日期部分,时间部分都用00:00:00,锁定的日期包括beginDate当天,不包括endDate当天
	 */
	public Map<String, Date> getLockBeginAndEndDate(Date checkinTime, Date checkoutTime) {
		try {
			Date newBeginDate = DateUtil.addHours(checkinTime, -12);
			Date newEndDate = DateUtil.addSecounds(DateUtil.addHours(checkoutTime, -12), -1);
			newEndDate = DateUtil.addDays(newEndDate, 1);
			
			Map<String, Date> map = new HashMap<>(2);
			map.put("beginDate", DateUtil.trimTimeFromDate(newBeginDate));
			map.put("endDate", DateUtil.trimTimeFromDate(newEndDate));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 按照早上6:00之前占昨天的房态,6:00之后占今天的房态
	 * @param checkinTime
	 * @param checkoutTime
	 * @return
	 */
	public Map<String, Date> getLockBeginAndEndDate6(Date checkinTime, Date checkoutTime) {
		try {
			Date newBeginDate = DateUtil.addHours(checkinTime, -6);
			Date newEndDate = DateUtil.addSecounds(DateUtil.addHours(checkoutTime, -6), -1);
//			newEndDate = DateUtil.addDays(newEndDate, 1);
			
			Map<String, Date> map = new HashMap<>(2);
			map.put("beginDate", DateUtil.trimTimeFromDate(newBeginDate));
			map.put("endDate", DateUtil.trimTimeFromDate(newEndDate));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public Map<String, Date> getLockBeginAndEndDate612(Date checkinTime, Date checkoutTime) {
		try {
			Date newBeginDate = DateUtil.addSecounds(DateUtil.addHours(checkinTime, -6), -1);
			Date newEndDate = DateUtil.addSecounds(DateUtil.addHours(checkoutTime, -12), -1);
//			newEndDate = DateUtil.addDays(newEndDate, 1);
			
			Map<String, Date> map = new HashMap<>(2);
			map.put("beginDate", DateUtil.trimTimeFromDate(newBeginDate));
			map.put("endDate", DateUtil.trimTimeFromDate(newEndDate));
			return map;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	/**
	 * 微信前端只传入日期,要将日期转换成带有时间的. <br>
	 * 传入入离日期(仅仅是日期,例如2015-11-12~2015-11-13表示包括2015-11-12但不包括2015-11-13,即预占2015-11-12的房态),获得checkinTime和checkoutTime
	 * @param beginDateStr 传入2015-11-12这种格式,不要传2015-11-12 17:34:51
	 * @param endDateStr 传入2015-11-12这种格式,不要传2015-11-12 17:34:51
	 * @return
	 */
	public Map<String, Date> getCheckinAndCheckoutTime(String beginDateStr, String endDateStr) {
		// 只传日期,不要时间
		// 结束日期肯定比开始日期至少大一天
		// 超过当天十二点,则开始日期不能是昨天
		// 场景:①现在是2015-11-17早9:00,传入17-18号,时间是17 14:00~18:12:00,传入18-19号,时间是18 14:00~19 12:00     ②现在是2015-11-17下午15:03,传入17-18号,时间是17 15:03~18 12:00,传入18-19号,时间是18 14:00~19 12:00
		// 场景:③现在是2015-11-17早9:00,传入16-17号,时间是17 9:00~17 12:00,传入16-18号,时间是17 9:00~18 12:00  ④现在是2015-11-17下午15:03,传入16-17号,抛错,超过中午12:00开始日期不能是昨天.
		try {
			// 只传日期,不要时间
			if(beginDateStr.contains(":") || endDateStr.contains(":")) {
				throw new RuntimeException("入参格式错误,beginDateStr" + beginDateStr + ",endDateStr" + endDateStr);
			}
			// 结束日期肯定比开始日期至少大一天
			Date beginDate = DateUtil.parseDate(beginDateStr);
			Date endDate = DateUtil.parseDate(endDateStr);
			if (DateUtil.compareDate(beginDate, endDate) > 0) {
				throw new RuntimeException("开始时间和结束时间非法");
			}
			// 当前时间超过12点,入参beginDateStr不能是昨天的日期(前端有校验,后端再校验)
			Date now = DateUtil.getCurDateTime();
			Date yestoday = DateUtil.addDays(now, -1);
			String str = DateUtil.getYearMonDayStr_(now) + " 12:00";
			Date today120000 = DateUtil.parseDateTimeNoSec(str);// 当天中午十二点整
			int comp = DateUtil.compareDateTime(now, today120000);
			boolean isBeginDateIsYestoday = DateUtil.isEqualDate(beginDate, yestoday);
			if(comp >= 0 && isBeginDateIsYestoday) {
				throw new RuntimeException("超过十二点不能选择昨天的日期");
			}
			
			if (isBeginDateIsYestoday) {
				// 开始日期加一天,开始日期的时间是当前日期的时间部分(为了避免提前退房的情况导致时间重叠),结束日期仍然是当天
				Date tomorrowBegin = DateUtil.addDays(beginDate, 1);
				String checkinHourMinSecStr = DateUtil.dateToString(now, DateUtil.hrMinSec);
				String checkinTimeStr = DateUtil.dateToString(tomorrowBegin, DateUtil.yrMonDay) + checkinHourMinSecStr;
				String checkoutTimeStr = DateUtil.dateToString(endDate, DateUtil.yrMonDay) + "120000";
				Date checkinTime = DateUtil.stringToDate(checkinTimeStr, DateUtil.yrMonDayHrMinSec); // 包括时间的开始时间
				Date checkoutTime = DateUtil.stringToDate(checkoutTimeStr, DateUtil.yrMonDayHrMinSec);// 包括时间的结束时间
				Map<String, Date> map = new HashMap<>(2);
				map.put("checkinTime", checkinTime);
				map.put("checkoutTime", checkoutTime);
				return map;
			} else {
				String checkinHourMinSecStr = "";
				String s = DateUtil.getYearMonDayStr_(now) + " 14:00";
				Date today140000 = DateUtil.parseDateTimeNoSec(s);
				int c = DateUtil.compareDateTime(now, today140000);
				int co = DateUtil.compareDate(beginDate, now);
				if (c < 0 || co > 0) {// 这是允许客户提前退房导致的判断复杂性上升.不能写死是now,如果now是12:00前的,去呼呼会锁定到昨天,也不能写死是14:00,因为客户可以在15:00提前退房,退房后房态释放,下个用户可以预定,若写死14:00的话会导致时间重叠而新建订单失败.
					checkinHourMinSecStr = "140000";
				} else {
					checkinHourMinSecStr = DateUtil.dateToString(now, DateUtil.hrMinSec);
				}
				String checkinTimeStr = DateUtil.dateToString(beginDate, DateUtil.yrMonDay) + checkinHourMinSecStr;
				String checkoutTimeStr = DateUtil.dateToString(endDate, DateUtil.yrMonDay) + "120000";
				Date checkinTime = DateUtil.stringToDate(checkinTimeStr, DateUtil.yrMonDayHrMinSec); // 包括时间的开始时间
				Date checkoutTime = DateUtil.stringToDate(checkoutTimeStr, DateUtil.yrMonDayHrMinSec);// 包括时间的结束时间
				Map<String, Date> map = new HashMap<>(2);
				map.put("checkinTime", checkinTime);
				map.put("checkoutTime", checkoutTime);
				return map;
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	/**
	 * 查询出符合条件非删除状态的房间
	 * @param checkinTime
	 * @param checkoutTime
	 */
	public List<Map<String, Object>> queryFreeRoom(Date checkinTime, Date checkoutTime) {
		try {
			// 获得入离时间具体要锁定的日期
			Map<String, Date> result = this.getLockBeginAndEndDate(checkinTime, checkoutTime);
			Date beginDate = result.get("beginDate");
			Date endDate= result.get("endDate");
			
			String beginDateStr = DateUtil.getYearMonDayStr_(beginDate);
			String endDateStr = DateUtil.getYearMonDayStr_(endDate);
			String sql = "SELECT a.room_id roomId,a.chain_id chainId,a.chain_name chainName,a.chain_addr chainAddr,a.room_type_name roomTypeName,a.room_name roomName,a.room_floor roomFloor,a.area roomArea,"
					+ "a.house_type houseType,a.province province,a.city city,a.district district,a.addr roomAddr,a.title title,a.lodger_count lodgerCount,a.bed_count bedCount"
					+ " FROM xb_room a INNER JOIN (SELECT room_id,COUNT(*) FROM xb_calendar_daily b WHERE b.cur_date>=? AND b.cur_date<? AND b.stat=? "
					+ "GROUP BY room_id HAVING COUNT(*)=DATEDIFF(?,?)) t ON a.room_id=t.room_id WHERE a.flag=?";
			
			List<Map<String, Object>> list = daoUtil.calendarDailyDao.queryMapList(sql, new Object[] { beginDateStr, endDateStr, AppConstants.CalendarDaily_stat.FREE_1, endDateStr,
					beginDateStr, AppConstants.Room_flag.NORMAL_1 });
			
			return list;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("查询空档房间出错:", e);
		}
	}
	
	/**
	 * 查询出所有非删除状态的房间,是否符合条件有标记
	 * @param checkinTime
	 * @param checkoutTime
	 */
	public List<Map<String, Object>> queryFreeRoomContainNotFreeRoom(Date checkinTime, Date checkoutTime) {
		try {
			// 获得入离时间具体要锁定的日期
			Map<String, Date> result = this.getLockBeginAndEndDate(checkinTime, checkoutTime);
			Date beginDate = result.get("beginDate");
			Date endDate= result.get("endDate");
			
			String beginDateStr = DateUtil.getYearMonDayStr_(beginDate);
			String endDateStr = DateUtil.getYearMonDayStr_(endDate);
			// cannotChgRoomFlag 1表示不可换该房间,0可换成该房间
			String sql = "SELECT ISNULL(t.room_id) cannotChgRoomFlag,a.room_id roomId,a.chain_id chainId,a.chain_name chainName,a.chain_addr chainAddr,a.room_type_name roomTypeName,a.room_name roomName,a.room_floor roomFloor,a.area roomArea,"
					+ "a.house_type houseType,a.province province,a.city city,a.district district,a.addr roomAddr,a.title title,a.lodger_count lodgerCount,a.bed_count bedCount"
					+ " FROM xb_room a LEFT JOIN (SELECT room_id,COUNT(*) FROM xb_calendar_daily b WHERE b.cur_date>=? AND b.cur_date<? AND b.stat=? "
					+ "GROUP BY room_id HAVING COUNT(*)=DATEDIFF(?,?)) t ON a.room_id=t.room_id WHERE a.flag=?";
			
			List<Map<String, Object>> list = daoUtil.calendarDailyDao.queryMapList(sql, new Object[] { beginDateStr, endDateStr, AppConstants.CalendarDaily_stat.FREE_1, endDateStr,
					beginDateStr, AppConstants.Room_flag.NORMAL_1 });
			
			return list;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("查询空档房间出错:", e);
		}
	}
	
	
	/**
	 * 重载方法,如果查出了该时间段的记录,则可以这么检查
	 * @param listCalendarUnit
	 * @return
	 */
	public boolean checkCanBook(List<CalendarUnit> listCalendarUnit) {
		try {
			if (listCalendarUnit == null || listCalendarUnit.isEmpty()) {
				return false;
			}
			boolean flag = true;
			for (CalendarUnit calendarUnit : listCalendarUnit) {
				if (calendarUnit.getLockStat() != AppConstants.CalendarDaily_stat.FREE_1) {
					flag = false;
					break;
				}
			}
			return flag;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	// 操作订单的日历
	public JSONObject operOrder(String roomId, Date checkinTime, Date checkoutTime, OperCodeOrder operCodeOrder, String datagram) {
		try {
			exceptionHandler.getLog().info("操作订单的日历,roomId:" + roomId + ",checkinTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime)
					+ ",checkoutTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkoutTime) + ",operCodeOrder:" + operCodeOrder);
			int operateCode = -1;
			switch (operCodeOrder) {
			case NEW_ORDER:
				operateCode = AppConstants.RoomStateOperCode.NEW_ORDER;
				return roomStateTool.editCalendarBatch(checkinTime, checkoutTime, operateCode, roomId, datagram);
			case CANCEL_ORDER_NOT_CHECKIN:
				operateCode = AppConstants.RoomStateOperCode.CANCEL_ORDER_NOT_CHECKIN;
				break;
			case CANCEL_ORDER_HAS_CHECKIN:
				operateCode = AppConstants.RoomStateOperCode.CANCEL_ORDER_HAS_CHECKIN;
				break;
			case CANCEL_ORDER_UN_PAID:
				operateCode = AppConstants.RoomStateOperCode.CANCEL_ORDER_UN_PAID;
				break;
			default:
				throw new RuntimeException("类型错误");
			}
			return roomStateTool.editCalendar(checkinTime, checkoutTime, operateCode, Integer.parseInt(roomId), datagram);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("操作房态异常:" + operCodeOrder, e);
		}
	}
	
	// 操作入住单的日历
	public JSONObject operCheckin(int roomId, Date checkinTime, Date checkoutTime, OperCodeCheckin operCodeCheckin, String datagram) {
		try {
			exceptionHandler.getLog().info("操作订单的日历,roomId:" + roomId + ",checkinTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime)
			+ ",checkoutTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkoutTime) + ",operCodeCheckin:" + operCodeCheckin);
			int operateCode = -1;
			switch (operCodeCheckin) {
			case CHECKIN:
				operateCode = AppConstants.RoomStateOperCode.CHECKIN;
				break;
			case CHECKOUT:
				checkinTime = DateUtil.getCurDateTime();// 后面讨论该接口在退房/停用结束/维修结束的时候传的不是入店时间,而是实际的离店时间,即当前时间
				exceptionHandler.getLog().info("退房改变了入离时间checkinTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime));
				operateCode = AppConstants.RoomStateOperCode.CHECKOUT;
				break;
			case OVERSTAY_CHECKOUT:
				checkinTime = DateUtil.getCurDateTime();// 后面讨论该接口在退房/停用结束/维修结束的时候传的不是入店时间,而是实际的离店时间,即当前时间
				exceptionHandler.getLog().info("续住退房改变了入离时间checkinTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime));
				operateCode = AppConstants.RoomStateOperCode.OVERSTAY_CHECKOUT;
				break;
			default:
				throw new RuntimeException("类型错误");
			}
			return roomStateTool.editCalendar(checkinTime, checkoutTime, operateCode, roomId, datagram);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("操作房态异常:" + operCodeCheckin, e);
		}
	}
		
	// 操作维修单的日历
	public JSONObject operFix(int roomId, Date checkinTime, Date checkoutTime, OperCodeFix operCodeFix, String datagram) {
		try {
			exceptionHandler.getLog().info("操作订单的日历,roomId:" + roomId + ",checkinTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime)
			+ ",checkoutTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkoutTime) + ",operCodeFix:" + operCodeFix);
			int operateCode = -1;
			switch (operCodeFix) {
			case NEW_FIX:
				operateCode = AppConstants.RoomStateOperCode.NEW_FIX;
				break;
			case FIX_CANCEL:
				operateCode = AppConstants.RoomStateOperCode.FIX_CANCEL;
				break;
			case FIX_BEGIN:
				operateCode = AppConstants.RoomStateOperCode.FIX_BEGIN;
				break;
			case FIX_END:
				checkinTime = DateUtil.getCurDateTime();// 后面讨论该接口在退房/停用结束/维修结束的时候传的不是入店时间,而是实际的离店时间,即当前时间
				exceptionHandler.getLog().info("维修单改变了入离时间checkinTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime));
				operateCode = AppConstants.RoomStateOperCode.FIX_END;
				break;
			default:
				throw new RuntimeException("类型错误");
			}
			return roomStateTool.editCalendar(checkinTime, checkoutTime, operateCode, roomId, datagram);
			
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("操作房态异常:" + operCodeFix, e);
		}
	}
	// 操作停用单的日历
	public JSONObject operStop(int roomId, Date checkinTime, Date checkoutTime, OperCodeStop operCodeStop, String datagram) {
		try {
			exceptionHandler.getLog().info("操作订单的日历,roomId:" + roomId + ",checkinTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime)
			+ ",checkoutTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkoutTime) + ",operCodeStop:" + operCodeStop);
			int operateCode = -1;
			switch (operCodeStop) {
			case NEW_STOP:
				operateCode = AppConstants.RoomStateOperCode.NEW_STOP;
				break;
			case STOP_CANCEL:
				operateCode = AppConstants.RoomStateOperCode.STOP_CANCEL;
				break;
			case STOP_BEGIN:
				operateCode = AppConstants.RoomStateOperCode.STOP_BEGIN;
				break;
			case STOP_END:
				checkinTime = DateUtil.getCurDateTime();// 后面讨论该接口在退房/停用结束/维修结束的时候传的不是入店时间,而是实际的离店时间,即当前时间
				exceptionHandler.getLog().info("停用单改变了入离时间checkinTime:" + DateUtil.getYearMonDayHrMinSecStr_(checkinTime));
				operateCode = AppConstants.RoomStateOperCode.STOP_END;
				break;
			default:
				throw new RuntimeException("类型错误");
			}
			return roomStateTool.editCalendar(checkinTime, checkoutTime, operateCode, roomId, datagram);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("操作房态异常:" + operCodeStop, e);
		}
	}
	
	public void generateCalendarV2(int roomId, Integer defaultPriceCentUnit,Integer howManyGen) {
		try {
			// 要产生多少天的日历
			int days = 365 * 2;
			if (howManyGen != null) {
				days = howManyGen.intValue();
			}
			AssertHelper.notNull("默认价格不能为空", defaultPriceCentUnit);
			
			Date now = DateUtil.getCurDateTime();
			XbCalendarDaily calendarDaily = daoUtil.calendarDailyDao.findLatestByRoomId(roomId);// 查最新的日历
			Date nowDate = DateUtil.getCurDate();
			if (calendarDaily != null) {
				nowDate = DateUtil.parseDate(calendarDaily.getCurDate());
				nowDate = DateUtil.addDays(nowDate, 1);// 如果不能于null,要从最后一天的下一天开始
			}
			List<XbCalendarDaily> calendarDailyList = new ArrayList<>();
			for (int i = 0; i < days; i++) {
				Date d = DateUtil.addDays(nowDate, i);
				XbCalendarDaily cal = new XbCalendarDaily();
				cal.setRoomId(roomId);
				cal.setCurMonth(DateUtil.getYearMonStr_(d));
				cal.setCurDate(DateUtil.getYearMonDayStr_(d));
				cal.setStat(CalendarDaily_stat.FREE_1);
				cal.setPrice(defaultPriceCentUnit);
				cal.setCreateDtm(now);
				cal.setLastModDtm(now);
				calendarDailyList.add(cal);
			}
			daoUtil.calendarDailyDao.batchInsertEntity(calendarDailyList, "calId", true);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("创建日历失败",e);
		}
	}
	
	
	
	/**
	 * 查询某个时间段的日历,效率较高,不会每一天都查库
	 * 
	 * @param roomId
	 * @param checkinTime
	 * @param checkoutTime
	 * @return 按照时间从小到大的房间日历列表
	 */
	public List<CalendarUnit> queryRoomCalendarByPeriod(int roomId, Date checkinTime, Date checkoutTime) {
		try {
			List<RemoteCalendarUnit> list = roomStateTool.queryFutureRoomStat(roomId, checkinTime, checkoutTime);
			return this.transRemote2CalendarUnit(list);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 转换 List<RemoteCalendarUnit> 为 List<CalendarUnit>
	 * @param remoteList
	 * @return
	 */
	private List<CalendarUnit> transRemote2CalendarUnit(List<RemoteCalendarUnit> remoteList) {
		try {
			int sz = remoteList.size();
			List<CalendarUnit> list = new ArrayList<>(sz);
			for (RemoteCalendarUnit calendarDaily : remoteList) {
				int lockStat = calendarDaily.getState();
				int price = calendarDaily.getPrice();
				String date = calendarDaily.getCalendarDate();
				CalendarUnit calendarUnit = new CalendarUnit(lockStat, price, date);
				list.add(calendarUnit);
			}
			return list;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
