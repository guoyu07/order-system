package cn.com.xbed.app.service.chgroom;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckinExt;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.vo.CalendarUnit;
import cn.com.xbed.app.bean.vo.ChgRoomUnitVo;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.ImageCommon;
import cn.com.xbed.app.service.LodgerService;
import cn.com.xbed.app.service.OmsUserService;
import cn.com.xbed.app.service.OperLogCommon;
import cn.com.xbed.app.service.OrderMgntCommon;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.ljh.CleanSystemService;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.impl.CheckoutType;
import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.service.roomstatemodule.RoomStateModuleBase;
import cn.com.xbed.app.service.roomstatemodule.RoomStateTool;
import cn.com.xbed.app.service.smsmodule.CommonSms;

@Service
@Transactional
public class ChgRoomService {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private ImageCommon imageCommon;
	@Resource
	private CommonService commonService;
	@Resource
	private LodgerService lodgerService;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private OperLogCommon operLogCommon;
	@Resource
	private OmsUserService omsUserService;
	@Resource
	private CleanSystemService cleanSystemService;
	@Resource
	private RoomStateTool roomStateTool;
	@Resource
	private RoomStateModuleBase roomStateModuleBase;
	@Resource
	private OrderUtil orderUtil;
	@Resource
	private CommonSms commonSms;
	@Resource
	private EventService eventService;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ChgRoomService.class));

	// 换房后依然是本订单,但是用新的
	public void chgRoom(String account, List<ChgRoomUnitVo> chgRoomOperList) {
		try {
			// 检查换房
			this.checkChgRoom(chgRoomOperList.get(0));

			// 查询数据
			ChgRoomUnitVo chgRoomOper = chgRoomOperList.get(0);
			int oriCheckinId = chgRoomOper.getOldCheckinId();
			int newRoomId = chgRoomOper.getNewRoomId();

			XbCheckin oriCheckinInfo = daoUtil.checkinDao.findByPk(oriCheckinId);
			XbOrder oriOrderInfo = daoUtil.orderMgntDao.findByPk(oriCheckinInfo.getOrderId());

			// 建新入住单
			Map<String, Object> out = this.newChgRoomOrder(oriCheckinId, newRoomId);
			int newCheckinId = ((XbCheckin) out.get("newCheckinInfo")).getCheckinId();

			// 处理旧单(退房或取消)
			this.handleOldOrder(oriCheckinId);

			// 记录日志
			int userId = omsUserService.queryOmsUserId(account);
			int newOrderId = oriOrderInfo.getOrderId();// 后来模型改变,换房后沿用相同的订单号,这里是原订单的订单号
			operLogCommon.saveOperLogChgroom(userId, oriOrderInfo.getOrderId(), oriCheckinId, newOrderId, newCheckinId, "");
			
			// 抛出事件
			eventService.throwChgRoomEvent(oriCheckinId, newCheckinId);
		} catch (Exception e) {
			exceptionHandler.getLog().info("OMS换房$失败" + ",account" + account + "chgOperList[" + chgRoomOperList + "]");
			throw exceptionHandler.logServiceException("换房失败", e);
		}
	}

	// 处理旧入住单
	public void handleOldOrder(int oriCheckinId) {
		exceptionHandler.getLog().info("处理旧入住单,oriCheckinId:" + oriCheckinId + " start==========");
		boolean isAlreadyCheckin = this.isAlreadyCheckin(oriCheckinId);
		exceptionHandler.getLog().info(isAlreadyCheckin);
		if (isAlreadyCheckin) {// 原入住单退房
			orderUtil.checkout(oriCheckinId, AppConstants.Checkin_checkoutType.OTHER_4, CheckoutType.CHG_ROOM);
		} else {// 取消原入住单(只要通知去呼呼,并且本地释放房态即可)
			roomStateModuleBase.roomStateCancelOneRoom(oriCheckinId);
		}
		exceptionHandler.getLog().info("处理旧入住单,oriCheckinId:" + oriCheckinId + " start==========");
	}

	// 建新入住单
	public Map<String, Object> newChgRoomOrder(int oriCheckinId, int newRoomId) {
		Map<String, Object> result = new HashMap<>();
		try {
			/*
			 * 逻辑: ............................................
			 * 1、新入住单的入离时间,跟原来的入住单一样或接续 ......................
			 * 2、新入住单是未入住的状态,原来的入住单是入住状态也不自动帮忙办理入住......................
			 * 3、新入住单的价格跟原入住单一样......................
			 * 4、新入住单的入住人lodger_id和名字、电话跟原入住单一样 5、新入住单标记为chg_room_flag新房
			 * 6、新入住单同时创建自己的xb_checkin_ext 7、新入住单记住父入住单ID(即原入住单)
			 * 8、旧入住单标记chg_room_flag为旧房 9、旧入住单记住子入住单ID(即新单)
			 */
			exceptionHandler.getLog().info("建新入住单,oriCheckinId:" + oriCheckinId + ",newRoomId" + newRoomId + " start=============");
			Map<String, Date> d = this.getChgRoomPeriod(oriCheckinId);
			Date newOrderStartTime = d.get("startTime");// 新订单开始时间
			Date newOrderEndTime = d.get("endTime");// 新订单结束时间

			XbCheckin oriCheckinInfo = daoUtil.checkinDao.findByPk(oriCheckinId);
			XbOrder oriOrderInfo = daoUtil.orderMgntDao.findByPk(oriCheckinInfo.getOrderId());
			
			XbCheckin newCheckin = new XbCheckin();
			// newCheckin.setActualCheckinTime(actualCheckinTime);
			// newCheckin.setActualCheckoutTime(actualCheckoutTime);
			// newCheckin.setCheckinId(checkinId);
			newCheckin.setCheckinTime(newOrderStartTime);
			newCheckin.setCheckoutTime(newOrderEndTime);
			// newCheckin.setCheckoutType(checkoutType);// 默认即可
			newCheckin.setChgRoomFlag(AppConstants.Checkin_chgRoomFlag.NEW_ROOM_2);// 是新房
			// newCheckin.setChildCheckinId(childCheckinId);// 暂无
			newCheckin.setChkinPrice(oriCheckinInfo.getChkinPrice());// 跟原来的一样
			newCheckin.setContactPhone(oriCheckinInfo.getContactPhone());
			newCheckin.setCreateTime(DateUtil.getCurDateTime());
			newCheckin.setLodgerId(oriCheckinInfo.getLodgerId());
			newCheckin.setLodgerName(oriCheckinInfo.getLodgerName());
			newCheckin.setNightCount(calendarCommon.getNightCount(newOrderStartTime, newOrderEndTime));
			// newCheckin.setOpenPwd(openPwd);// 入住阶段更新
			newCheckin.setOrderId(oriOrderInfo.getOrderId());
			// newCheckin.setOtaStayId(otaStayId);// 由房态系统更新
			newCheckin.setParentCheckinId(oriCheckinInfo.getCheckinId());
			newCheckin.setBatch(oriCheckinInfo.getBatch());
			newCheckin.setRemark("换房,新房");
			newCheckin.setRoomId(newRoomId);
			newCheckin.setStat(AppConstants.Checkin_stat.NEW_0);// 未入住状态
			// newCheckin.setLastModDtm(lastModDtm);
			Long newCheckinId = (Long) daoUtil.checkinDao.addAndGetPk(newCheckin);
			newCheckin.setCheckinId(newCheckinId.intValue());

			// 保存xb_checkin_ext
			XbCheckinExt checkinExt = new XbCheckinExt();
			checkinExt.setCheckinId(newCheckin.getCheckinId());
			Long checkinExtId = (Long) daoUtil.checkinExtDao.addAndGetPk(checkinExt);
			checkinExt.setExtId(checkinExtId.intValue());

			// 改旧xb_checkin.chg_room_flag
			XbCheckin oldCheckin = new XbCheckin();
			oldCheckin.setCheckinId(oriCheckinInfo.getCheckinId());
			oldCheckin.setChgRoomFlag(AppConstants.Checkin_chgRoomFlag.ORI_ROOM_1);
			oldCheckin.setChildCheckinId(newCheckin.getCheckinId());
			daoUtil.checkinDao.updateEntityByPk(oldCheckin);

			// 同步去呼呼
			roomStateModuleBase.roomStateNewCheckin(newCheckinId.intValue());
			
			// 写入住人表xb_checkiner
			List<XbCheckiner> checkinerList = daoUtil.checkinerDao.findByCheckinId(oriCheckinInfo.getCheckinId());
			for (XbCheckiner xbCheckiner : checkinerList) {
				xbCheckiner.setCreateDtm(DateUtil.getCurDateTime());
				xbCheckiner.setCheckinerId(null);
				xbCheckiner.setCheckinId(newCheckinId.intValue());
				daoUtil.checkinerDao.addAndGetPk(xbCheckiner);
			}
			
			result.put("newCheckinInfo", newCheckin);
			
			exceptionHandler.getLog().info("建新入住单 end=============,oriCheckinId:" + oriCheckinId + ",newRoomId" + newRoomId);
			return result;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException("换房新建订单失败", e);
		}
	}

	// 检查是否能换房
	private void checkChgRoom(ChgRoomUnitVo chgRoomOper) {
		try {
			int oriCheckinId = chgRoomOper.getOldCheckinId();
			int newRoomId = chgRoomOper.getNewRoomId();
			exceptionHandler.getLog().info("OMS换房,入参,旧入住单oldCheckinId:" + oriCheckinId + ",换房newRoomId:" + newRoomId);

			XbCheckin oriCheckinInfo = daoUtil.checkinDao.findByPk(oriCheckinId);// 原入住单
			XbOrder oriOrderInfo = daoUtil.orderMgntDao.findByPk(oriCheckinInfo.getOrderId());// 原订单
			int orderStat = oriOrderInfo.getStat();
			int checkinStat = oriCheckinInfo.getStat();

			if (oriOrderInfo.getOrderType() == AppConstants.Order_orderType.SELF_FORBID_2) {
				throw exceptionHandler.newErrorCodeException("-1", "房态控制单不能换房");
			}
			if (oriOrderInfo.getRoomCount() != null && oriOrderInfo.getRoomCount() > 1) {
				throw exceptionHandler.newErrorCodeException("-3", "一单多房不能换房");
			}
			if (!(orderStat == AppConstants.Order_stat.PAYED_1
					&& (checkinStat == AppConstants.Checkin_stat.NEW_0 || checkinStat == AppConstants.Checkin_stat.CHECKIN_1))) {
				throw exceptionHandler.newErrorCodeException("-5", "入住单或订单状态错误");
			}
			if (DateUtil.compareDate(DateUtil.getCurDateTime(), oriCheckinInfo.getCheckoutTime()) > 0) {
				throw exceptionHandler.newErrorCodeException("-5", "入住单或订单状态错误");
			}
			if (oriOrderInfo.getSource() == AppConstants.Order_source.QUNAR_1 && checkinStat == AppConstants.Checkin_stat.NEW_0) {
				throw exceptionHandler.newErrorCodeException("-7", "未入住的去哪儿订单不能办理换房");
			}
			Map<String, Date> d = this.getChgRoomPeriod(oriCheckinId);
			Date startTime = d.get("startTime");// 新订单开始时间
			Date endTime = d.get("endTime");// 新订单结束时间
			List<CalendarUnit> roomCalList = calendarCommon.queryRoomCalendarByPeriod(newRoomId, startTime, endTime);// 每天的房态和价格
			if (!calendarCommon.checkCanBook(roomCalList)) {
				throw exceptionHandler.newErrorCodeException("-9", "换的房间被占");
			}
		} catch (Exception e) {
			exceptionHandler.getLog().error("OMS换房检查失败,原因:", e);
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 获得换房后新入住单的入离时间
	public Map<String, Date> getChgRoomPeriod(int checkinId) {
		Map<String, Date> resultMap = new HashMap<>();
		try {
			Date now = DateUtil.getCurDateTime();
			Date startTime = null;
			Date endTime = null;
			XbCheckin checkinInfo = daoUtil.checkinDao.findByPk(checkinId);
			if (!this.isAlreadyCheckin(checkinId)) {// 已支付未入住
				startTime = checkinInfo.getCheckinTime();
				endTime = checkinInfo.getCheckoutTime();
			} else {// 已支付已入住
				startTime = now;
				endTime = checkinInfo.getCheckoutTime();
			}
			resultMap.put("startTime", startTime);
			resultMap.put("endTime", endTime);
			exceptionHandler.getLog().info("旧checkinInfo:" + checkinInfo);
			exceptionHandler.getLog().info("获得换房后新房的时间段,startTime:" + startTime + ",endTime:" + endTime);
			return resultMap;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 是否已经入住
	public boolean isAlreadyCheckin(int checkinId) {
		try {
			String sql = "SELECT stat FROM xb_checkin WHERE checkin_id=?";
			List<Map<String, Object>> resultList = daoUtil.checkinDao.queryMapList(sql, new Object[] { checkinId }, true);
			AssertHelper.notEmpty(resultList, "数据异常,查询非空,checkinId:" + checkinId);
			int checkinStat = (int) resultList.get(0).get("stat");
			if (checkinStat == AppConstants.Checkin_stat.NEW_0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
