package cn.com.xbed.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbManSendsmsRec;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbOrderCtrip;
import cn.com.xbed.app.bean.XbOrderPromo;
import cn.com.xbed.app.bean.XbOrderStop;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.bean.vo.CalendarUnit;
import cn.com.xbed.app.bean.vo.OtaChannelNewOrdVo;
import cn.com.xbed.app.bean.vo.OtaChannelNewOrdVo.Checkiner;
import cn.com.xbed.app.bean.vo.PromoNewOrd;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.util.RandomUtil;
import cn.com.xbed.app.commons.util.RestCallHelper;
import cn.com.xbed.app.commons.util.SqlCreatorTool;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.pager.ListPage;
import cn.com.xbed.app.commons.pager.PageHelper;
import cn.com.xbed.app.commons.pager.Pager;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.invoicemodule.vo.Invoice;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.NewCheckinOut;
import cn.com.xbed.app.service.localordermodule.vo.NewOrderOut;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.impl.FromType;
import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.service.smsmodule.CommonSms;
import cn.com.xbed.app.service.stopmodule.StopModule;
import cn.com.xbed.app.service.stopmodule.vo.StopEntity;

@Service
@Transactional
public class OmsMgntService {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private StopModule stopModule;
	@Resource
	private CommonSms commonSms;
	@Resource
	private CommonService commonService;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private OrderUtil orderUtil;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	@Resource
	private EventService eventService;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OmsMgntService.class));

	//  活动的,不开发票
	public Map<String, Object> addPromoOrder(PromoNewOrd promoNewOrd) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			exceptionHandler.getLog().info("新创建携程订单,cTripNewOrdVo:" + promoNewOrd);
			String orderMan = promoNewOrd.getOrderMan();
			String orderManMobile = promoNewOrd.getOrderManMobile();
			String checkinMan = promoNewOrd.getCheckinManName();
			String checkinManMobile = promoNewOrd.getCheckinManMobile();
			String memo = promoNewOrd.getMemo();
			int userId = promoNewOrd.getUserId();
			int roomId = promoNewOrd.getRoomId();
			
			String userAcct = promoNewOrd.getUserAcct();
			String checkinTimeStr = promoNewOrd.getCheckinTime();
			String checkoutTimeStr = promoNewOrd.getCheckoutTime();
			AssertHelper.notNull(orderMan, orderManMobile, memo, userAcct,checkinTimeStr, checkoutTimeStr, "传参异常");
			exceptionHandler.getLog().info("新创建活动订单,orderManMobile:" + orderManMobile + ",orderMan:" + orderMan);
			exceptionHandler.getLog().info("新创建活动订单,checkinManMobile:" + checkinManMobile + ",checkinMan:" + checkinMan);
			exceptionHandler.getLog().info("新创建活动订单,roomId:" + roomId);
			
			// 计算具体的入离时间
			Map<String, Date> result = calendarCommon.getCheckinAndCheckoutTime(checkinTimeStr, checkoutTimeStr);
			Date checkinTime = result.get("checkinTime");
			Date checkoutTime = result.get("checkoutTime");
			
			// 检查业务
			List<CalendarUnit> roomCalList = calendarCommon.queryRoomCalendarByPeriod(roomId, checkinTime, checkoutTime);// 每天的房态和价格
			if (!calendarCommon.checkCanBook(roomCalList)) {
				throw exceptionHandler.newErrorCodeException("-2", "不能预订,房间被占");
			}
			
			// 创建下单人
			String randomPass = RandomUtil.getRandomStr(6);
			Map<String, Object> lodgerMap = commonService.judgeIfNeedCreateLodger(orderManMobile, orderMan, "携程订单创建下单人",
					AppConstants.Lodger_source.OTA_2, randomPass, AppConstants.Lodger_hasChgPwd.NOT_YET_1,true);
			int bookLodgerId = (int) lodgerMap.get("lodgerId");// 联系人
			boolean isNewCreate = (boolean) lodgerMap.get("isNewCreate");
			
			// 创建订单
			int stat = AppConstants.Order_stat.PAYED_1;
			int source = AppConstants.Order_source.OMS_4;
			int payType = AppConstants.Order_payType.UNKNOW_2;
			int orderType = AppConstants.Order_orderType.PROMO_8;
			int totalPriceCentUnit = 100;// 活动的价格发给去呼呼的时候最少是1元,时候再把订单和入住单的价格改成0元
			String bookLodgerName = orderMan;
			String bookLodgerMobile = orderManMobile;
			String otaOrderNo = null;
			OrderInput orderInput = new OrderInput(stat, source, payType, orderType, totalPriceCentUnit, bookLodgerId,
					bookLodgerName, bookLodgerMobile, checkinTime, checkoutTime, otaOrderNo, 0);
			
			List<CheckinInput> checkinInputList = new ArrayList<>();
			int checkinPrice = totalPriceCentUnit;
			int chkinPriceCentUnit = checkinPrice;
			String otaStayId = null;
			
			
			// 创建入住人
			String randomPassCheckin = RandomUtil.getRandomStr(6);
			Map<String, Object> lodgerMapCheckin = commonService.judgeIfNeedCreateLodger(checkinManMobile, checkinMan, "携程订单创建入住人",
					AppConstants.Lodger_source.PROMO_6, randomPassCheckin, AppConstants.Lodger_hasChgPwd.NOT_YET_1,true);
			int checkinLodgerId = (int) lodgerMapCheckin.get("lodgerId");// 联系人
			boolean isNewCreateCheckinId = (boolean) lodgerMapCheckin.get("isNewCreate");
			
			
			CheckinInput checkinInput = new CheckinInput(roomId, checkinLodgerId, checkinMan, checkinManMobile, checkinTime, checkoutTime,
					chkinPriceCentUnit, otaStayId);
			checkinInputList.add(checkinInput);
			Invoice invoiceInfo = null;// 无需发票
			FromType fromType = FromType.FROM_LOCAL;
			NewOrderOut out = orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, fromType);
			int orderId = out.getOrderInfo().getOrderId();
			List<NewCheckinOut> newCheckinList = out.getNewCheckinList();
			int checkinId = newCheckinList.get(0).getCheckinInfo().getCheckinId();
			
			// 记录到写成订单表
			String datagram = JsonHelper.toJSONString(promoNewOrd);
			XbOrderPromo orderPromo = new XbOrderPromo(orderId, out.getOrderInfo().getOrderNo(), memo, DateUtil.getCurDateTime(), datagram, userId, userAcct);
			daoUtil.orderPromoDao.add(orderPromo);
			
			// 将xb_order.total_price     xb_checkin.chkin_price
			XbOrder newOrder = new XbOrder();
			newOrder.setOrderId(orderId);
			newOrder.setTotalPrice(0);
			daoUtil.orderMgntDao.updateEntityByPk(newOrder);
			XbCheckin newCheckin = new XbCheckin();
			newCheckin.setCheckinId(checkinId);
			newCheckin.setChkinPrice(0);
			daoUtil.checkinDao.updateEntityByPk(newCheckin);
			
			// 下发短信,维护xb_checkin_ext/xb_order_ext
			exceptionHandler.getLog().info("新创建活动订单,抛出事件:");
			maintainModuleBase.markHasSendBookSms(orderId);
			eventService.throwBookAfterPaySuccEvent(orderId);
			
			resultMap.put("orderInfo", this.queryPromoOrder(out.getOrderInfo().getOrderNo(), true));
			return resultMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	// 查询活动的订单号
	public List<Map<String, Object>> queryPromoOrder(String orderNo, boolean... qryFromMaster) {
		try {
			String orderField = SqlCreatorTool.getSelectFields(XbOrder.class, "a");
			String sql = "SELECT " + orderField;
			sql += ",b.promo_id promoId,b.memo memo,b.user_id userId,b.user_acct userAcct,b.create_dtm ctriCreateDtm ";
			sql += ",r.room_id roomId,r.chain_id chainId,r.room_name roomName,r.chain_name chainName";
			sql += ",c.checkin_id checkinId,c.lodger_name checkinLodgerName,c.contact_phone checkinMobile,c.checkin_time checkinTime,c.checkout_time checkoutTime ";
			sql += "FROM xb_order a INNER JOIN xb_checkin c ON a.order_id=c.order_id INNER JOIN xb_room r ON c.room_id=r.room_id LEFT JOIN xb_order_promo b ON a.order_id=b.order_id WHERE a.order_type=8";
			List<Object> params = new ArrayList<>();
			if (StringUtils.isNotBlank(orderNo)) {
				sql += " AND b.order_no=?";
				params.add(orderNo);
			}
			return daoUtil.orderCtripDao.queryMapList(sql, params.toArray(), qryFromMaster);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	
	// 要返回订单新增订单信息给前端
	//{"checkinTime":"2016-1-1","checkinerList":[{"checkinManMobile":"13431093332","checkinManName":"王尼玛","checkinPrice":28000,"roomId":668},{"checkinManMobile":"13431098888","checkinManName":"王你妹","checkinPrice":39000,"roomId":669}],
	//"checkoutTime":"2016-1-2","otaChannelOrderNo":"trip9999888","memo":"这是备注,订单的备注","orderMan":"王大锤","orderManMobile":"13042014179","totalPrice":100,"userAcct":"xbedadmin","userId":1,channel:1}
	public Map<String, Object> addOtaChannelOrder(OtaChannelNewOrdVo otaChannelNewOrdVo) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			exceptionHandler.getLog().info("新创建携程订单,otaChannelNewOrdVo:" + otaChannelNewOrdVo);
			String orderMan = otaChannelNewOrdVo.getOrderMan();
			String orderManMobile = otaChannelNewOrdVo.getOrderManMobile();
			String otaChannelOrderNo = otaChannelNewOrdVo.getOtaChannelOrderNo();
			String memo = otaChannelNewOrdVo.getMemo();
			int totalPrice = otaChannelNewOrdVo.getTotalPrice();
			int userId = otaChannelNewOrdVo.getUserId();
			
			String userAcct = otaChannelNewOrdVo.getUserAcct();
			String checkinTimeStr = otaChannelNewOrdVo.getCheckinTime();
			String checkoutTimeStr = otaChannelNewOrdVo.getCheckoutTime();
			List<Checkiner> list = otaChannelNewOrdVo.getCheckinerList();
			AssertHelper.notNull(orderMan, orderManMobile, otaChannelOrderNo, memo, userAcct,checkinTimeStr, checkoutTimeStr, "传参异常");
			AssertHelper.notEmpty(list, "传参异常");
			exceptionHandler.getLog().info("新创建携程订单,otaChannelOrderNo:" + otaChannelOrderNo);
			
			// 计算具体的入离时间
			Map<String, Date> result = calendarCommon.getCheckinAndCheckoutTime(checkinTimeStr, checkoutTimeStr);
			Date checkinTime = result.get("checkinTime");
			Date checkoutTime = result.get("checkoutTime");
			
			// 检查业务
			int totalCheckinPrice = 0;
			for (Checkiner checkiner : list) {
				int checkinPrice = checkiner.getCheckinPrice();
				totalCheckinPrice += checkinPrice;
			}
			if (totalCheckinPrice != totalPrice) {
				throw exceptionHandler.newErrorCodeException("-1", "传入的订单总价和入住单总价之和不等");
			}
			for (Checkiner checkiner : list) {
				int roomId = checkiner.getRoomId();
				XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(roomId);
				List<CalendarUnit> roomCalList = calendarCommon.queryRoomCalendarByPeriod(roomId, checkinTime, checkoutTime);// 每天的房态和价格
				if (!calendarCommon.checkCanBook(roomCalList)) {
					throw exceptionHandler.newErrorCodeException("-2", "不能预订,房间被占,房间号:" + roomInfo.getRoomName());
				}
			}
			
			// 创建下单人
			String randomPass = RandomUtil.getRandomStr(6);
			Map<String, Object> lodgerMap = commonService.judgeIfNeedCreateLodger(orderManMobile, orderMan, "携程订单创建下单人",
					AppConstants.Lodger_source.OTA_2, randomPass, AppConstants.Lodger_hasChgPwd.NOT_YET_1,true);
			int bookLodgerId = (int) lodgerMap.get("lodgerId");// 联系人
			boolean isNewCreate = (boolean) lodgerMap.get("isNewCreate");
			
			// 创建订单
			int stat = AppConstants.Order_stat.PAYED_1;
			int source = orderMgntCommon.getOtaChannel(otaChannelNewOrdVo.getChannel());
			int payType = AppConstants.Order_payType.UNKNOW_2;
			int orderType = AppConstants.Order_orderType.CUST_ORDER_1;
			int totalPriceCentUnit = totalPrice;
			String bookLodgerName = orderMan;
			String bookLodgerMobile = orderManMobile;
			String otaOrderNo = null;
			OrderInput orderInput = new OrderInput(stat, source, payType, orderType, totalPriceCentUnit, bookLodgerId,
					bookLodgerName, bookLodgerMobile, checkinTime, checkoutTime, otaOrderNo,0);
			
			List<CheckinInput> checkinInputList = new ArrayList<>();
			for (Checkiner checkiner : list) {
				String checkinManName = checkiner.getCheckinManName();
				String checkinManMobile = checkiner.getCheckinManMobile();
				int roomId = checkiner.getRoomId();
				int checkinPrice = checkiner.getCheckinPrice();
				int chkinPriceCentUnit = checkinPrice;
				String otaStayId = null;
				
				// 创建入住人
				String randomPassCheckin = RandomUtil.getRandomStr(6);
				Map<String, Object> lodgerMapCheckin = commonService.judgeIfNeedCreateLodger(checkinManMobile, checkinManName, "携程订单创建入住人",
						AppConstants.Lodger_source.OTA_2, randomPassCheckin, AppConstants.Lodger_hasChgPwd.NOT_YET_1,true);
				int checkinLodgerId = (int) lodgerMapCheckin.get("lodgerId");// 联系人
				boolean isNewCreateCheckinId = (boolean) lodgerMapCheckin.get("isNewCreate");
				
				CheckinInput checkinInput = new CheckinInput(roomId, checkinLodgerId, checkinManName, checkinManMobile, checkinTime, checkoutTime,
						chkinPriceCentUnit, otaStayId);
				checkinInputList.add(checkinInput);
			}
			Invoice invoiceInfo = null;// 无需发票
			FromType fromType = FromType.FROM_LOCAL;
			NewOrderOut out = orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, fromType);
			int orderId = out.getOrderInfo().getOrderId();
			
			
			// 记录到写成订单表
			String datagram = JsonHelper.toJSONString(otaChannelNewOrdVo);
			XbOrderCtrip orderCtrip = new XbOrderCtrip(otaChannelOrderNo, orderId, out.getOrderInfo().getOrderNo(), memo, DateUtil.getCurDateTime(), datagram, userId, userAcct);
			daoUtil.orderCtripDao.add(orderCtrip);
			
			
			// 抛出事件,维护xb_checkin_ext/xb_order_ext
			exceptionHandler.getLog().info("新创建携程订单,抛出事件:" + otaChannelOrderNo);
			maintainModuleBase.markHasSendBookSms(orderId);
			eventService.throwBookAfterPaySuccEvent(orderId);
			
			
			resultMap.put("orderInfo", this.queryOtaChannelOrder(null, out.getOrderInfo().getOrderNo(), true));
			return resultMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	// 查询携程的订单号
	public List<Map<String, Object>> queryOtaChannelOrder(String otaChannelNo, String orderNo, boolean... qryFromMaster) {
		try {
			String orderField = SqlCreatorTool.getSelectFields(XbOrder.class, "a");
			String sql = "SELECT " + orderField;
			sql += ",b.ctrip_id ctripId,b.ctrip_no ctripNo,b.memo memo,b.user_id userId,b.user_acct userAcct,b.create_dtm ctriCreateDtm ";
			sql += ",r.room_id roomId,r.chain_id chainId,r.room_name roomName,r.chain_name chainName";
			sql += ",c.checkin_id checkinId,c.lodger_name checkinLodgerName,c.contact_phone checkinMobile,c.checkin_time checkinTime,c.checkout_time checkoutTime ";
			sql += "FROM xb_order a INNER JOIN xb_checkin c ON a.order_id=c.order_id INNER JOIN xb_room r ON c.room_id=r.room_id LEFT JOIN xb_order_ctrip b ON a.order_id=b.order_id WHERE a.source in (5,7)";
			List<Object> params = new ArrayList<>();
			if (StringUtils.isNotBlank(otaChannelNo)) {
				sql += " AND b.ctrip_no=?";
				params.add(otaChannelNo);
			}
			if (StringUtils.isNotBlank(orderNo)) {
				sql += " AND b.order_no=?";
				params.add(orderNo);
			}
			return daoUtil.orderCtripDao.queryMapList(sql, params.toArray(), qryFromMaster);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public List<Map<String, Object>> addStopSheet(StopEntity stopEntity) {
		try {
			XbOrderStop orderStop = stopModule.addStopSheet(stopEntity);
			String field = SqlCreatorTool.getSelectFields(XbOrderStop.class);
			String sql = "select "+field+" from xb_order_stop where stop_id=?";
			List<Map<String, Object>> list = daoUtil.logStopSheetDao.queryMapList(sql,new Object[]{orderStop.getStopId()}, true);
			for (Map<String, Object> map : list) {
				map.put("status", this.getStopStatus(orderStop.getStopStat()));
			}
			return list;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public void cancelStopSheet(int stopId, int userId, String userAcct) {
		try {
			stopModule.cancelStopSheet(stopId, userId, userAcct);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	public ListPage queryStopSheet(int thisPage, int pageSize) {
		try {
			if (thisPage <= 0 || pageSize <= 0) {
				throw new RuntimeException("非法入参thisPage[" + pageSize + "],pageSize[" + pageSize + "]");
			}
			ListPage listPage = new ListPage();
			String fields = SqlCreatorTool.getSelectFields(XbOrderStop.class);
			String sql = "SELECT " + fields + " FROM xb_order_stop ORDER BY create_dtm DESC LIMIT ?,?";
			List<Map<String, Object>> listMap = daoUtil.orderMgntDao.queryMapList(sql,
					new Object[] { PageHelper.getStartIndex(thisPage, pageSize), pageSize });

			for (Map<String, Object> map : listMap) {
				int stopStat = (int) map.get("stopStat");
				if (this.canCancel(stopStat)) {
					map.put("oper", 1);// 1-有取消禁售的,2-没有
				} else {
					map.put("oper", 2);
				}
				Date stopBegin = (Date) map.get("stopBegin");
				Date stopEnd = (Date) map.get("stopEnd");
				map.put("status", this.getStopStatus(stopStat));//1未开始 2进行中 3已结束 4已取消
			}

			Pager pager = new Pager();
			pager.setPageSize(pageSize);
			pager.setThisPage(thisPage);
			listPage.setPager(pager);
			listPage.setList(listMap);
			return listPage;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	// 1未开始 2进行中 3已结束 4已取消
	private int getStopStatus(int stopStat) {//0新建 1开始 2结束 3停用取消
		//1未开始 2进行中 3已结束 4已取消
		switch (stopStat) {
		case 0:
			return 1;
		case 1:
			return 2;
		case 2:
			return 3;
		case 3:
			return 4;
		}
		return 1;
	}
	
	private boolean canCancel(int stopStat) {
		if (stopStat == AppConstants.OrderStop_stopStat.NEW_0 || stopStat == AppConstants.OrderStop_stopStat.BEGIN_1) {
			return true;
		}
		return false;
	}

	// -1数据问题,-2开门密码为空数据问题
	public int queryDoorPwd(int checkinId, Map<String, Object> resultMap) {
		try {
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			if (checkinInfo == null) {
				RestCallHelper.fillParams(resultMap, -1, null, "数据问题");
				return -1;
			}
			String openpwd = checkinInfo.getOpenPwd();
			// if (openpwd == null || openpwd.length() == 0) {
			// RestCallHelper.fillParams(resultMap, -2, null, "开门密码为空数据问题");
			// return -2;
			// }
			RestCallHelper.fillSuccessParams(resultMap, openpwd);
			return 1;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// OMS发送短信并记录下发送记录
	public void sendSmsAndLog(String mobile, String content) {
		try {
			int recordId = commonSms.sendSms(mobile, content);

			Date now = DateUtil.getCurDateTime();
			XbManSendsmsRec manSendsmsRec = new XbManSendsmsRec();
			manSendsmsRec.setCreateDtm(now);
			manSendsmsRec.setMobile(mobile);
			manSendsmsRec.setSmsContent(content);
			manSendsmsRec.setRecordId(recordId);
			manSendsmsRec.setUserId(-1);// 不知道是哪个管理员
			daoUtil.manSendsmsDao.addAndGetPk(manSendsmsRec);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
