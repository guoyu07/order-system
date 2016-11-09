package cn.com.xbed.app.service.roomstatemodule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.vo.RemoteCalendarUnit;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.HttpHelper;
import cn.com.xbed.app.commons.util.HttpHelper.HeaderPair;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.enu.HTTPMethod;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
@Component
public class RoomStateTool {
	@Resource
	private DaoUtil daoUtil;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(RoomStateTool.class));
	private String url1 = "http://192.168.1.183:6869/v1/api/room/updateStatus";//修改房间状态
	private String url2 = "http://192.168.1.183:6869/v1/api/room/query/{roomId}";//查询当前房态
	private String url3 = "http://192.168.1.183:6869/v1/api/room/query/remoteRooms/{roomId}";//查询远期房态
	private String url4 = "http://192.168.1.183:6869/v1/api/room/update/{roomId}/price";//修改远期房价
	private String url5 = "http://192.168.1.183:6869/v1/api/rooms/updateStatus";//修改房间状态(批量)
	
	public static final int SUCCESS_CODE = 21020000;
	public static final String SECRET_KEY = "fewt42t80972345hnj98g34";
	public static final String ROOM_STATE_SECRET_KEY = "9f8796ae4c214acea70bba6ea6630966";// 房态接口的密钥
	
	public static final Comparator<RemoteCalendarUnit> sortRemoteCalendarUnitByDateAsc = new Comparator<RemoteCalendarUnit>() {
		@Override
		public int compare(RemoteCalendarUnit o1, RemoteCalendarUnit o2) {
			Date d1 = DateUtil.parseDate(o1.getCalendarDate());
			Date d2 = DateUtil.parseDate(o2.getCalendarDate());
			int c = DateUtil.compareDateTime(d1, d2);
			if (c > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	};
	
	
	
	public String queryUrl1() {
		return daoUtil.sysParamDao.getValueByParamKey("url.calendar.update_status");
	}
	public String queryUrl2() {
		return daoUtil.sysParamDao.getValueByParamKey("url.calendar.query_current_status");
	}
	public String queryUrl3() {
		return daoUtil.sysParamDao.getValueByParamKey("url.calendar.query_future_status");
	}
	public String queryUrl4() {
		return daoUtil.sysParamDao.getValueByParamKey("url.calendar.edit_room_price");
	}
	public String queryUrl5() {
		return daoUtil.sysParamDao.getValueByParamKey("url.calendar.update_status_batch");
	}
	public String queryTransferRoomIdUrl() {
		return daoUtil.sysParamDao.getValueByParamKey("url.roominfo.transfer_room_id");
	}
	
	
	
	
	/**
	 * 转换room_id,从业主app的roomId转成本地的
	 * @param ownerRoomId
	 * @return
	 */
	public int transferRoomId(int ownerRoomId) {
		try {
			String transferRoomIdUrl = queryTransferRoomIdUrl();
			transferRoomIdUrl = transferRoomIdUrl.replaceAll("\\{roomId\\}", ownerRoomId + "");

			Map<String, Object> params = new HashMap<>();
			params.put("roomId", ownerRoomId);
			exceptionHandler.getLog().info("【a】转换roomId,入参: " + transferRoomIdUrl + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(transferRoomIdUrl, params, HTTPMethod.GET, headerPair);
			exceptionHandler.getLog().info("【a】转换roomId,出参: " + retStr);
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				int roomId = obj.getIntValue("data");
				return roomId;
			} else {
				throw new RuntimeException("转换roomId错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		}
	}
	
	
	/**
	 * 修改房间状态
	 * @param checkinTime 开始时间
	 * @param checkoutTime 结束时间
	 * @param operateCode 操作代码
	 * @param roomId 房间ID
	 * @param datagram 其它业务操作json数据
	 */
	public JSONObject editCalendar(Date checkinTime, Date checkoutTime, int operateCode, int roomId, String datagram) {
		try {
			String checkinTimeStr = DateUtil.getYearMonDayHrMinSecStr_(checkinTime);
			String checkoutTimeStr = DateUtil.getYearMonDayHrMinSecStr_(checkoutTime);
			return this.editCalendar(checkinTimeStr, checkoutTimeStr, operateCode, roomId, datagram);
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		}
	}

	/**
	 * 修改房间状态<br>
	 * 
	 * @param checkinTime
	 *            必须传年月日时分秒 2015-11-28 14:00:00
	 * @param checkoutTime
	 *            必须传年月日时分秒 2015-11-29 12:00:00
	 * @param operateCode
	 *            参照xb_condition操作
	 * @param roomId
	 * @return RoomApi 的结果数据
	 */
	public JSONObject editCalendar(String checkinTime, String checkoutTime, int operateCode, int roomId, String datagram) {
		try {
			url1 = queryUrl1();
			if (StringUtils.isBlank(checkinTime) || StringUtils.isBlank(checkoutTime) //
					|| !checkinTime.contains(":") || !checkoutTime.contains(":")) {
				throw new RuntimeException("传参错误");
			}
			Map<String, Object> params = new HashMap<>();
			params.put("beginDateTime", checkinTime);
			params.put("endDateTime", checkoutTime);
			params.put("operateCode", operateCode);
			params.put("roomId", roomId);
			params.put("other", datagram);
			exceptionHandler.getLog().info("【1】修改房间状态,入参: " + url1 + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", ROOM_STATE_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(url1, params, HTTPMethod.PUT, headerPair);
			exceptionHandler.getLog().info("【1】修改房间状态,出参: " + retStr);
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				return obj;
			} else {
				throw new RuntimeException("修改房间状态错误,返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("修改房间状态错误", e);
		}
	}

	// 新增订单的时候支持批量新增
	public JSONObject editCalendarBatch(Date checkinTime, Date checkoutTime, int operateCode, String roomId, String datagram) {
		try {
			String checkinTimeStr = DateUtil.getYearMonDayHrMinSecStr_(checkinTime);
			String checkoutTimeStr = DateUtil.getYearMonDayHrMinSecStr_(checkoutTime);
			return this.editCalendarBatch(checkinTimeStr, checkoutTimeStr, operateCode, roomId, datagram);
		} catch (Exception e) {
			throw exceptionHandler.logToolException(e);
		}
	}
	
	// 新增订单的时候支持批量新增
	public JSONObject editCalendarBatch(String checkinTime, String checkoutTime, int operateCode, String roomId, String datagram) {
		try {
			url5 = queryUrl5();
			if (StringUtils.isBlank(checkinTime) || StringUtils.isBlank(checkoutTime) //
					|| !checkinTime.contains(":") || !checkoutTime.contains(":")) {
				throw new RuntimeException("传参错误(批量)");
			}
			Map<String, Object> params = new HashMap<>();
			params.put("beginDateTime", checkinTime);
			params.put("endDateTime", checkoutTime);
			params.put("operateCode", operateCode);
			params.put("roomId", roomId);
			params.put("other", datagram);
			exceptionHandler.getLog().info("【11】修改房间状态(批量),入参: " + url5 + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", ROOM_STATE_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(url5, params, HTTPMethod.PUT, headerPair);
			exceptionHandler.getLog().info("【11】修改房间状态(批量),出参: " + retStr);
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				return obj;
			} else {
				throw new RuntimeException("修改房间状态错误(批量),返回码:" + retCode + ",msg:" + msg);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("修改房间状态错误(批量)", e);
		}
	}

	/**
	 * 查询当前房态<br>
	 * 
	 * @param roomId
	 * @return 当前房状态状态，（1 干净房 ，2 脏房 ， 3 已入住， 4 停用，5 维修）,查不到该房间的时候抛错
	 */
	public int queryCurentRoomStat(int roomId) {
		try {
			url2 = queryUrl2();
			
			url2 = url2.replaceAll("\\{roomId\\}", roomId + "");
			exceptionHandler.getLog().info("【2】查询当前房态,入参: " + url2 + ",params:" + null);
			HeaderPair headerPair = new HeaderPair("secret", ROOM_STATE_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(url2, null, HTTPMethod.GET, headerPair);
			exceptionHandler.getLog().info("【2】查询当前房态,出参: " + retStr);

			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			JSONObject data = obj.getJSONObject("data");
			if (retCode == SUCCESS_CODE) {// 成功
				if (data != null && StringUtils.isNotBlank(data.getString("state"))) {
					return Integer.parseInt(data.getString("state"));
				} else {
					throw new RuntimeException("查询值错误,state:" + data.getString("state"));
				}
			} else {
				throw new RuntimeException("查询当前房态错误,返回码:" + retCode);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("查询当前房态错误", e);
		}
	}

	/**
	 * 查询远期房态<br>
	 * 
	 * @param roomId
	 * @param beginDate
	 *            包括这天日期
	 * @param dateNumber
	 *            传1,2...等正整数
	 * @return 正常返回size>1的,查无数据size=0,网络故障抛出异常,返回非"成功"的代码也抛出异常
	 */
	public List<RemoteCalendarUnit> queryFutureRoomStat(int roomId, String checkinTime, String checkoutTime) {
		try {
			url3 = queryUrl3();
			url3 = url3.replaceAll("\\{roomId\\}", roomId + "");
			Map<String, Object> params = new HashMap<>();
			params.put("beginDateTime", checkinTime);
			params.put("endDateTime", checkoutTime);
			params.put("roomId", roomId);
			exceptionHandler.getLog().info("【3】查询远期房态,入参: " + url3 + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", ROOM_STATE_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(url3, params, HTTPMethod.GET, headerPair);
			exceptionHandler.getLog().info("【3】查询远期房态,出参: " + retStr);
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			JSONArray array = obj.getJSONArray("data");
			if (retCode == SUCCESS_CODE) {// 成功
				int sz = array.size();
				List<RemoteCalendarUnit> list = new ArrayList<>();
				for (int i = 0; i < sz; i++) {
					JSONObject o = array.getJSONObject(i);
					String calendarDate = o.getString("calendarDate");
					int price = o.getIntValue("price");
					int state = o.getIntValue("state");
					int troomId = o.getIntValue("roomId");
					RemoteCalendarUnit calendarUnit = new RemoteCalendarUnit(state, price, calendarDate, troomId);
					list.add(calendarUnit);
				}
				Collections.sort(list, sortRemoteCalendarUnitByDateAsc);
				return list;
			} else {
				throw new RuntimeException("查询远期房态错误,返回码:" + retCode);
			}
		} catch (Exception e) {
			throw exceptionHandler.logToolException("查询远期房态错误", e);
		}
	}

	public List<RemoteCalendarUnit> queryFutureRoomStat(int roomId, Date checkinTime, Date checkoutTime) {
		String checkinTimeStr = DateUtil.getYearMonDayHrMinSecStr_(checkinTime);
		String checkoutTimeStr = DateUtil.getYearMonDayHrMinSecStr_(checkoutTime);
		return this.queryFutureRoomStat(roomId, checkinTimeStr, checkoutTimeStr);
	}
	/**
	 * 修改远期房价 <br>
	 * 
	 * @param beginDate
	 *            包括这天日期
	 * @param dateNumber
	 *            传1,2...等正整数
	 * @param price
	 *            传0,1,2...等正整数,单位分
	 * @param roomId
	 */
	public boolean editRoomPrice(int roomId, String beginDate, int dateNumber, int price) {
		try {
			url4 = queryUrl4();
			url4 = url4.replaceAll("\\{roomId\\}", roomId + "");
			Map<String, Object> params = new HashMap<>();
			params.put("beginDate", beginDate);
			params.put("dateNumber", dateNumber);
			params.put("roomId", roomId);
			params.put("price", price);
			exceptionHandler.getLog().info("【4】修改远期房价,入参: " + url4 + ",params:" + params);
			HeaderPair headerPair = new HeaderPair("secret", ROOM_STATE_SECRET_KEY);
			String retStr = HttpHelper.callRemoteMethod(url4, params, HTTPMethod.PUT, headerPair);
			exceptionHandler.getLog().info("【4】修改远期房价,出参: " + retStr);
			JSONObject obj = JsonHelper.parseJSONStr2JSONObject(retStr);
			int retCode = obj.getIntValue("retCode");
			String msg = obj.getString("msg");
			if (retCode == SUCCESS_CODE) {// 成功
				return true;
			} else {
				throw new RuntimeException("修改远期房价错误,返回码:" + retCode + ",msg:" + msg);
			}

		} catch (Exception e) {
			throw exceptionHandler.logToolException("修改远期房价错误", e);
		}
	}

	
	public static void main(String[] args) {
		RoomStateTool t = new RoomStateTool();
		//System.out.println(t.editCalendar("2015-11-28 14:00:00", "2015-11-29 12:00:00",AppConstants.RoomStateOperCode.NEW_ORDER, 1));
	}

	
}
