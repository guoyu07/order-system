package cn.com.xbed.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.SvSysParam;
import cn.com.xbed.app.bean.XbAddition;
import cn.com.xbed.app.bean.XbChain;
import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbQhhSyncLog;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.bean.XbTempRoom;
import cn.com.xbed.app.bean.vo.NewRoomInfoVo;
import cn.com.xbed.app.bean.vo.NewRoomInfoVo.FaciPair;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.dao.QhhSyncLogDao;
import cn.com.xbed.app.dao.SysParamDao;
import cn.com.xbed.app.dao.TestDao;
import cn.com.xbed.app.dao.common.DaoUtil;

@Service
// @Transactional
public class TestService {
	@Resource
	private SysParamDao sysParamDao;
	@Resource
	private TestDao testDao;
	@Resource
	private QhhSyncLogDao qhhSyncLogDao;
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private RoomMgntService roomMgntService;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(TestService.class));

	/**
	 * 转换成正式的xb_room数据
	 */
	@Transactional
	public void trans2LocalRoom(int tmpRoomId) {
		try {
			XbTempRoom tempRoom = daoUtil.tempRoomDao.findByPk(tmpRoomId);
			AssertHelper.notNull(tempRoom, "不能为空,tmpRoomId:" + tmpRoomId);
			if (tempRoom.getStatus().equals("wait_check")) {
				throw exceptionHandler.newErrorCodeException("-1", "非待审核上线状态");
			}
			
			// 默认的xb_chain
			int default_chain_id = 1;
			
			// flag
			int default_flag = AppConstants.Room_flag.NORMAL_1;
			byte default_floor = 88;// 88楼
			
			XbChain chainInfo = daoUtil.chainMgntDao.findByPk(default_chain_id);
			
			// 默认价格1分钱
			int default_price = 1;
			
			String default_province = "广东省";
			String default_city = "广州市";
			String default_district = "荔湾区";
			
			
			// 设置值
			XbRoom roomInfo = new XbRoom();
			roomInfo.setRoomName(tempRoom.getName());
			roomInfo.setChainId(chainInfo.getChainId());// 暂定1
			roomInfo.setTitle("个性化标题,tmpRoomId:" + tmpRoomId);
			roomInfo.setChainName(chainInfo.getName());
			roomInfo.setAddr(chainInfo.getAddress());
			roomInfo.setFlag(default_flag);
			roomInfo.setCreateDtm(DateUtil.getCurDateTime());
			roomInfo.setBusiDate(DateUtil.getCurDateTime());
			roomInfo.setRoomTypeName("房型名");
			roomInfo.setBuildingId(0);
			roomInfo.setRoomTypeId(1);
			roomInfo.setRoomFloor(default_floor);
			roomInfo.setArea("50平");
//			roomInfo.setHouseType(houseType);
//			roomInfo.setStat(stat);
			roomInfo.setPrice(default_price);
//			roomInfo.setLocate();
			roomInfo.setProvince(default_province);
			roomInfo.setCity(default_city);
			roomInfo.setDistrict(default_district);
			roomInfo.setDescri("房间亮点描述");
			roomInfo.setPicId(1);
//			roomInfo.setPicCount(picCount);
//			roomInfo.setCurrency(currency);
			roomInfo.setTag("业主app过来的tag");
			roomInfo.setOwnerRoomId(tempRoom.getRoomId());
			
			
			
			
			
			
			
			
			
			
			XbAddition additionInfo = new XbAddition();
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			List<FaciPair> faciInfo = new ArrayList<>();
			FaciPair faciPair = new FaciPair(1, 1);// 不管,随便的
			faciInfo.add(faciPair);
			
			NewRoomInfoVo infoVo = new NewRoomInfoVo(roomInfo, faciInfo, additionInfo);
			roomMgntService.addRoomInfo(JsonHelper.toJSONString(infoVo));
		} catch (Exception e) {
			e.printStackTrace();
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	@Transactional
	public void debugSuperMan(Integer orderId, String orderNo, Integer checkinId) {
		try {
			if (orderId == null && StringUtils.isBlank(orderNo) && checkinId == null) {
				throw new RuntimeException("传参错误");
			}
			String sql = "select a.order_id from xb_order a,xb_checkin b where a.order_id=b.order_id";
			List<Object> params = new ArrayList<>();
			if (orderId != null) {
				sql += " and a.order_id=?";
				params.add(orderId);
			}
			if (StringUtils.isNotBlank(orderNo)) {
				sql += " and a.order_no=?";
				params.add(orderNo);
			}
			if (checkinId != null) {
				sql += " and a.checkin_id=?";
				params.add(checkinId);
			}
			List<Map<String, Object>> ll = daoUtil.additionDao.queryMapList(sql, params.toArray());
			if (!ll.isEmpty()) {
				int order_Id = (int) ll.get(0).get("order_id");
				XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(order_Id);
				JSONObject newOrder = getOrder(orderInfo);

				List<XbCheckin> checkList = daoUtil.checkinDao.findByOrderId(orderId);
				for (XbCheckin checkinInfo : checkList) {
					Map<String, Object> checkinInfoRoomInfo = new LinkedHashMap<>();
					XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInfo.getRoomId());
					XbChain chainInfo = daoUtil.chainMgntDao.findByPk(roomInfo.getChainId());
					XbLodger mainCheckinMan = daoUtil.lodgerDao.findByPk(checkinInfo.getLodgerId());
					checkinInfoRoomInfo.put("checkinInfo", checkinInfo);
					checkinInfoRoomInfo.put("mainCheckinMan", mainCheckinMan);
					checkinInfoRoomInfo.put("roomInfo", roomInfo);
					checkinInfoRoomInfo.put("chainInfo", chainInfo);
					List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findByCheckinId(checkinInfo.getCheckinId());
					checkinInfoRoomInfo.put("checkinerList", checkinerList);
					//list.add(checkinInfoRoomInfo);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw exceptionHandler.logServiceException(e);
		}
	}

//	public JSONObject getCheckin(XbCheckin full) {
//		XbCheckin newCheckin = new XbCheckin();
//		newCheckin.setCheckinId(full.getCheckinId());
//		newCheckin.setOrderId(full.getOrderId());
//		//newCheckin.setStat(stat);
//		newCheckin.setRoomId(full.getRoomId());
//		newCheckin.setCheckinTime(full.getCheckinTime());
//		newCheckin.setCheckoutTime(full.getCheckoutTime());
//		newCheckin.setActualCheckinTime(full.getActualCheckinTime());
//		newCheckin.setActualCheckoutTime(full.getActualCheckoutTime());
////		newCheckin.setChgRoomFlag(chgRoomFlag);
//		newCheckin.setOtaStayId(full.getOtaStayId());
////		newCheckin.set
//	}
	
	public JSONObject getOrder(XbOrder full) {
		XbOrder newOrder = new XbOrder();
		newOrder.setOrderId(full.getOrderId());
		newOrder.setOrderNo(full.getOrderNo());
		newOrder.setCreateTime(full.getCreateTime());
		newOrder.setLodgerId(full.getLodgerId());
		newOrder.setLodgerName(full.getLodgerName());
		newOrder.setLodgerMobile(full.getLodgerMobile());
		newOrder.setOtaOrderNo(full.getOtaOrderNo());
		String s = JsonHelper.toJSONString(newOrder);
		JSONObject obj = JsonHelper.parseJSONStr2JSONObject(s);
		obj.put("source", getSource(full.getSource()));
		obj.put("orderStat", getOrderStat(full.getStat()));
		obj.put("orderType", getOrderType(full.getOrderType()));
		return obj;
	}

	public String getOrderType(int orderType) {
		// 订单类型 1-客户订单 2-禁售单 5-测试订单 6-换房订单 7-业主订单 8-活动订单 【2的状态作废】
		String s = "";
		switch (orderType) {
		case 1:
			s = "客户订单[" + orderType + "]";
			break;
		case 2:
			s = "禁售单[" + orderType + "]";
			break;
		case 5:
			s = "测试订单[" + orderType + "]";
			break;
		case 6:
			s = "换房订单[" + orderType + "]";
			break;
		case 7:
			s = "业主订单[" + orderType + "]";
			break;
		case 8:
			s = "活动订单[" + orderType + "]";
			break;

		default:
			s = "其他[" + orderType + "]";
			break;
		}
		return s;
	}

	public String getOrderStat(int orderStat) {
		// 订单状态 0:未支付 1:已支付 2:取消(主动) 3:超时(被动) 4:取消未退款 5:取消且已退款
		String s = "";
		switch (orderStat) {
		case 0:
			s = "未支[" + orderStat + "]";
			break;
		case 1:
			s = "已支付[" + orderStat + "]";
			break;
		case 2:
			s = "取消(主动)[" + orderStat + "]";
			break;
		case 3:
			s = "超时取消(被动)[" + orderStat + "]";
			break;
		case 4:
			s = "取消未退款[" + orderStat + "]";
			break;
		case 5:
			s = "取消且已退款[" + orderStat + "]";
			break;

		default:
			s = "其他[" + orderStat + "]";
			break;
		}
		return s;
	}

	public String getSource(int source) {
		// 订单来源 0-微信端 1-去哪儿 2-IOS端 3-安卓端 4-OMS 5-携程
		String s = "";
		switch (source) {
		case 0:
			s = "微信[" + source + "]";
			break;
		case 1:
			s = "去哪儿[" + source + "]";
			break;
		case 2:
			s = "IOS端[" + source + "]";
			break;
		case 3:
			s = "安卓端[" + source + "]";
			break;
		case 4:
			s = "OMS端[" + source + "]";
			break;
		case 5:
			s = "携程[" + source + "]";
			break;

		default:
			s = "其他[" + source + "]";
			break;
		}
		return s;
	}

	@Transactional
	public void initNightCount() {
		try {
			String sql = "select * from xb_checkin where night_count is null or chkin_price is null";
			List<XbCheckin> list = daoUtil.checkinDao.queryForMultiRow(XbCheckin.class, sql, true);
			for (XbCheckin xbCheckin : list) {
				Date checkinTime = xbCheckin.getCheckinTime();
				Date checkoutTime = xbCheckin.getCheckoutTime();
				if (checkinTime != null && checkoutTime != null) {
					XbCheckin newCheckin = new XbCheckin();
					newCheckin.setCheckinId(xbCheckin.getCheckinId());
					if (xbCheckin.getNightCount() == null) {
						int nightCount = calendarCommon.getNightCount(checkinTime, checkoutTime);
						newCheckin.setNightCount(nightCount);
					}
					if (xbCheckin.getChkinPrice() == null) {
						String ssql = "select total_price from xb_order where order_id=?";
						List<Map<String, Object>> ll = daoUtil.checkinDao.queryMapList(ssql, new Object[] { xbCheckin.getOrderId() }, true);
						if (ll.size() == 1) {
							Integer chkinPrice = (Integer) ll.get(0).get("total_price");
							newCheckin.setChkinPrice(chkinPrice);
						}
					}
					daoUtil.checkinDao.updateEntityByPk(newCheckin);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 包括开始时间和结束时间
	 * 
	 * @param beginDate
	 * @param endDate
	 */
	@Transactional
	public void tongji(Date beginDate, Date endDate) {
		try {
			Date d1 = DateUtil.addDays(beginDate, -2);
			Date d2 = DateUtil.addDays(endDate, -2);
			String sql = "select * from xb_checkin where checkin_";

		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional
	public List<Map<String, Object>> tongjiOneDay(Date someDate) {
		try {
			String sql = "SELECT a.order_id,a.`order_no`,a.`stat` orderStat,b.`checkin_id`,b.`stat` checkinStat,b.`checkin_time`,b.`checkout_time`,a.`order_type`,"
					+ "a.`create_time` orderCreateTime,a.`total_price`,b.`chkin_price`,b.`room_id`"
					+ "FROM xb_order a,xb_checkin b WHERE a.`order_id`=b.`order_id` AND b.`checkin_time`<? AND b.`checkout_time`>?";

			return sysParamDao.queryMapList(sql, new Object[] { DateUtil.getYearMonDayStr_(someDate), DateUtil.getYearMonDayStr_(someDate) });
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}


	public int adminLogin(String name, String password) {
		try {
			SvSysParam debugSysParam = sysParamDao.getByParamKey("debug.admin_account");
			if (debugSysParam != null && StringUtils.isNotBlank(debugSysParam.getParamValue())) {
				JSONObject obj = JsonHelper.parseJSONStr2JSONObject(debugSysParam.getParamValue());
				String name1 = obj.getString("name");
				String password1 = obj.getString("password");
				if (name.equals(name1) && password.equals(password1)) {
					return 1;
				} else {
					return -1;
				}
			}
			return -1;
		} catch (Exception e) {
			return -1;
		}
	}

	@Transactional
	public void updateRoomState(Integer roomId, Integer operateCode, String date) {
		try {
			Date d = DateUtil.parseDate(date);
			String day = DateUtil.getYearMonDayStr_(d);

		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional
	public List<Map<String, Object>> queryOperateCode() {
		try {
			String sql = "select * from xb_condition";
			List<Map<String, Object>> list = testDao.queryMapList(sql);
			return list;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional
	public void test() {
		try {
			XbQhhSyncLog entity = new XbQhhSyncLog();
			entity.setLogId(95);
			entity.setDatagram("update by app");
			qhhSyncLogDao.updateEntityByPk(entity);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional
	public void query(int id) {
		try {
			XbQhhSyncLog syncLog = qhhSyncLogDao.findByPk(id);
			System.out.println(syncLog);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional
	public void test2() {
		try {
			XbQhhSyncLog entity = new XbQhhSyncLog();
			entity.setCreateDtm(DateUtil.getCurDateTime());
			entity.setDatagram("2");
			entity.setSucc(AppConstants.QhhSyncLog_succ.SUCC_1);
			entity.setOtaOrderNo("");
			entity.setOrderStatusCode("");
			entity.setOrderMan("");
			entity.setOrderMobile("");
			qhhSyncLogDao.add(entity);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public void mod(String jsonStr) {
		try {
			XbCheckiner entity = JsonHelper.parseJSONStr2T(jsonStr, XbCheckiner.class);
			testDao.updateEntityByPk(entity, "checkinerId");
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public Map<String, Object> queryOrderByPeriodAndRoomId(String start, String end, int roomId) {
		try {
			Date d = DateUtil.parseDate(start);
			Date d2 = DateUtil.parseDate(end);
			start = DateUtil.getYearMonDayStr_(d);
			end = DateUtil.getYearMonDayStr_(d2);
			String orderField = "a.order_id,a.stat orderStat";
			String sql = "SELECT " + orderField
					+ ",b.checkin_id,b.stat,date_format(b.checkin_time,'%Y-%m-%s %H:%i:%s') checkin_time,date_format(b.checkout_time,'%Y-%m-%s %H:%i:%s') checkout_time,b.lodger_name,b.contact_phone,b.room_id,date_format(b.create_time,'%Y-%m-%s %H:%i:%s') create_time FROM xb_order a,xb_checkin b WHERE a.order_id=b.order_id AND b.room_id=? AND DATE_FORMAT(b.checkin_time,'%Y-%m-%d')<=? AND DATE_FORMAT(b.checkout_time,'%Y-%m-%d')>=?";
			List<Map<String, Object>> data = testDao.queryMapList(sql, new Object[] { roomId, start, end });
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("xb_order.stat", "订单状态 0:未支付 1:已支付 2:取消(主动) 3:超时(被动) 4:取消未退款 5:取消且已退款");
			result.put("xb_order.order_type", "订单类型 1-客户订单 2-禁售单 5-测试订单 6-换房订单 7-业主订单 8-活动订单 【2的状态作废】");
			result.put("xb_checkin.stat", "状态 0:新建 1:已办理入住 2:已退房");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public Object initBaseData() {
		try {

			return null;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
