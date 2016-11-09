package cn.com.xbed.app.service.owner;

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

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.LogOwnerCreate;
import cn.com.xbed.app.bean.XbLodger;
import cn.com.xbed.app.bean.vo.CalendarUnit;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.EnumHelper;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.util.RandomUtil;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.invoicemodule.vo.Invoice;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.NewOrderOut;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;
import cn.com.xbed.app.service.logmodule.LogModule;
import cn.com.xbed.app.service.logmodule.enu.LogOwnerCreate_result;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.impl.FromType;
import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.service.roomstatemodule.RoomStateTool;
import cn.com.xbed.app.service.smsmodule.CommonSms;

@Service
@Transactional
public class OwnerService {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(OwnerService.class));
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonService commonService;
	@Resource
	private OrderUtil orderUtil;
	@Resource
	private CalendarCommon calendarCommon;
	@Resource
	private CommonSms commonSms;
	@Resource
	private LogModule logModule;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private EventService eventService;
	@Resource
	private RoomStateTool roomStateTool;

	public Map<String, Object> newOwner(String jsonStr) {
		boolean isNewCreate = false;
		int contactLodgerId = 0;
		boolean result = true;
		String name = "";
		String mobile = "";
		Map<String, Object> resultMap = new HashMap<>();
		try {
			JSONObject entity = JsonHelper.parseJSONStr2JSONObject(jsonStr);
			name = entity.getString("name");
			mobile = entity.getString("mobile");
			if (StringUtils.isBlank(name) || StringUtils.isBlank(mobile)) {
				throw exceptionHandler.newErrorCodeException("-1", "传参错误:名字和手机必传");
			}
			
			String randomPass = RandomUtil.getRandomStr(6);
			Map<String, Object> lodgerMap = commonService.judgeIfNeedCreateLodger(mobile, name,
					"业主APP创建的", AppConstants.Lodger_source.OWNER_5, randomPass,
					AppConstants.Lodger_hasChgPwd.NOT_YET_1, false);
			contactLodgerId = (int) lodgerMap.get("lodgerId");// 联系人
			isNewCreate = (boolean) lodgerMap.get("isNewCreate");
			
			// 如果原来就存在,又不需要升级,抛错
			if (!isNewCreate) {
				XbLodger lodgerInfo = daoUtil.lodgerDao.findByPk(contactLodgerId);
				if (lodgerInfo.getLodgerType() == AppConstants.Lodger_lodgerType.ONWER_1) {
					throw exceptionHandler.newErrorCodeException("-2", "已存在该业主,无需再注册");
				}
			} else {
				eventService.throwRegisterOwnerEvent(contactLodgerId);
			}
			
			// 升级类型
			XbLodger lodgerInfo = new XbLodger();
			lodgerInfo.setLodgerId(contactLodgerId);
			lodgerInfo.setLodgerType(AppConstants.Lodger_lodgerType.ONWER_1);
			daoUtil.lodgerDao.updateEntityByPk(lodgerInfo);
			
			resultMap.put("lodgerId", contactLodgerId);
			resultMap.put("isNewCreate", isNewCreate);
			resultMap.put("randomPass", randomPass);
		} catch (Exception e) {
			result = false;
			throw exceptionHandler.logServiceException(e);
		} finally {
			LogOwnerCreate owner = new LogOwnerCreate();
			owner.setCreateDtm(DateUtil.getCurDateTime());
			owner.setIsNewCreate(isNewCreate ? AppConstants.LogOwnerCreate_isNewCreate.YES_1 : AppConstants.LogOwnerCreate_isNewCreate.NO_0);
			owner.setLodgerId(contactLodgerId);
			owner.setOwnerMobile(mobile);
			owner.setOwnerName(name);
			owner.setParams("");
			owner.setResult(result ? EnumHelper.getEnumString(LogOwnerCreate_result.SUCC) : EnumHelper.getEnumString(LogOwnerCreate_result.FAIL));
			logModule.logOwnerCreate(owner);
		}
		return resultMap;
	}
	
	public void addOwnerOrder(String bookName, String bookMobile, String begin, String end, int ownerRoomId, String checkinName, String checkinMobile) {
		String s = String.format("订单需要创建新的入住人,bookName: %s,bookMobile: %s,begin: %s,end: %s,ownerRoomId: %s,checkinName: %s,checkinMobile: %s", bookName,
				bookMobile, begin, end, ownerRoomId, checkinName, checkinMobile);
		exceptionHandler.getLog().info(s);
		try {
			// 准备数据
			Map<String, Date> result = calendarCommon.getCheckinAndCheckoutTime(begin, end);
			Date arrivalTime = result.get("checkinTime");
			Date departTime = result.get("checkoutTime");
			
			// 转换roomId
			int roomId = roomStateTool.transferRoomId(ownerRoomId);
			exceptionHandler.getLog().info("转换前ownerRoomId:" + ownerRoomId + ",转换后roomId:" + roomId);

			// 检查能否预订
			List<CalendarUnit> roomCalList = calendarCommon.queryRoomCalendarByPeriod(roomId, arrivalTime, departTime);// 每天的房态和价格
			if (!calendarCommon.checkCanBook(roomCalList)) {
				throw exceptionHandler.newErrorCodeException("-2", "不能预订,房间被占");
			}
			// 是否业主
			XbLodger ownerInfo = daoUtil.lodgerDao.findByMobile(bookMobile);
			if (ownerInfo == null || ownerInfo.getLodgerType() != AppConstants.Lodger_lodgerType.ONWER_1) {
				throw exceptionHandler.newErrorCodeException("-3", "不是业主,不能下单");
			}
			
			// 是否要创建入住人
			boolean isNewCreate = false;
			int contactLodgerId = ownerInfo.getLodgerId();
			if (StringUtils.isNotBlank(checkinName) && StringUtils.isNotBlank(checkinMobile)) {
				exceptionHandler.getLog().info("创建入住人");
				String randomPass = RandomUtil.getRandomStr(6);
				Map<String, Object> lodgerMap = commonService.judgeIfNeedCreateLodger(checkinMobile, checkinName, "业主APP创建入住人",
						AppConstants.Lodger_source.OWNER_5, randomPass, AppConstants.Lodger_hasChgPwd.NOT_YET_1, false);
				contactLodgerId = (int) lodgerMap.get("lodgerId");
				isNewCreate = (boolean) lodgerMap.get("isNewCreate");
			}
			
			
			
			// 保存订单
			int stat = AppConstants.Order_stat.PAYED_1;
			int source = AppConstants.Order_source.OWNER_6;
			int payType = AppConstants.Order_payType.UNKNOW_2;
			int orderType = AppConstants.Order_orderType.ONWER_7;
			int totalPriceCentUnit = 100;// 业主发起
			int bookLodgerId = ownerInfo.getLodgerId();
			Date beginDateTime = arrivalTime;
			Date endDateTime = departTime;
			String otaOrderNo = null;
			OrderInput orderInput = new OrderInput(stat, source, payType, orderType, totalPriceCentUnit, bookLodgerId, bookName, bookMobile,
					beginDateTime, endDateTime, otaOrderNo, 0);

			List<CheckinInput> checkinInputList = new ArrayList<>();
			int checkinLodgerId = contactLodgerId;// 联系人lodgerId
			String actualCheckinName = bookName;
			String actualCheckinMobile = bookMobile;
			if (StringUtils.isNotBlank(checkinName) && StringUtils.isNotBlank(checkinMobile)) {
				actualCheckinName = checkinName;
				actualCheckinMobile = checkinMobile;
			}
			Date checkinBeginTime = arrivalTime;
			Date checkinEndTime = departTime;
			int chkinPriceCentUnit = totalPriceCentUnit;
			String otaStayId = null;
			CheckinInput checkinInput = new CheckinInput(roomId, checkinLodgerId, actualCheckinName, actualCheckinMobile, checkinBeginTime, checkinEndTime,
					chkinPriceCentUnit, otaStayId);
			checkinInputList.add(checkinInput);
			Invoice invoiceInfo = null;// 无需发票
			FromType fromType = FromType.ONWER;
			NewOrderOut out = orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, fromType);
			int orderId = out.getOrderInfo().getOrderId();
			
			// 发送短信
			if (isNewCreate) {
				eventService.throwRegisterOwnerEvent(contactLodgerId);
			}
			// 标记为发了预订成功短信
			maintainModuleBase.markHasSendBookSms(orderId);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
	
	public int sendSms(String mobile, String content) {
		try {
			return commonSms.sendSms(mobile, content);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
