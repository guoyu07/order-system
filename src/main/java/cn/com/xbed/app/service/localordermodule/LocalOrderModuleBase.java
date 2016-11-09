package cn.com.xbed.app.service.localordermodule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckinExt;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbOrderExt;
import cn.com.xbed.app.bean.XbRoom;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.RandomUtil;
import cn.com.xbed.app.commons.exception.AssertHelper;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CalendarCommon;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.NewCheckinOut;
import cn.com.xbed.app.service.localordermodule.vo.NewOrderOut;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;

@Service
@Transactional
public class LocalOrderModuleBase {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CalendarCommon calendarCommon;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(LocalOrderModuleBase.class));

	private String getMultiRoomIds(List<CheckinInput> checkinInputList) {
		if (checkinInputList != null && !checkinInputList.isEmpty()) {
			String multiRoomId = "";
			int sz = checkinInputList.size();
			for (int i = 0; i < sz; i++) {
				CheckinInput checkinInput = checkinInputList.get(i);
				String sep = i + 1 == sz ? "" : ",";
				multiRoomId += (checkinInput.getRoomId() + sep);
			}
			return multiRoomId;
		}
		return "";
	}

	/**
	 * 生成本地订单(不涉及锁房,同步OTA...)
	 * 
	 * @param orderInput
	 * @param checkinInputList
	 * @return 返回出参中最好只使用对象的主键,避免可能是null值
	 */
	public NewOrderOut localNewOrder(OrderInput orderInput, List<CheckinInput> checkinInputList) {
		try {
			AssertHelper.notEmpty(checkinInputList, "入参错误");
			AssertHelper.notNull(orderInput, "入参错误");

			// 保存xb_order
			String expire = daoUtil.sysParamDao.getValueByParamKey("ORDER_TIMEOUT_CONFIG");
			Date now = DateUtil.getCurDateTime();
			String orderNo = RandomUtil.getBookOrderNo();
			XbOrder newOrder = new XbOrder();
			newOrder.setOrderNo(orderNo);
			if (StringUtils.isNotBlank(orderInput.getOrderNo())) {
				newOrder.setOrderNo(orderInput.getOrderNo());
			}
			newOrder.setStat(orderInput.getStat());// 新订单
			newOrder.setLodgerId(orderInput.getBookLodgerId());// 预定人ID
			newOrder.setLodgerName(orderInput.getBookLodgerName());
			newOrder.setLodgerMobile(orderInput.getBookLodgerMobile());
			newOrder.setRoomId(this.getMultiRoomIds(checkinInputList));
			newOrder.setRoomCount(checkinInputList.size());// 预订的房间数
			//newOrder.setRoomPrice("");// 不再维护该字段
			newOrder.setTotalPrice(orderInput.getTotalPriceCentUnit());
			newOrder.setDiscountPrice(orderInput.getDiscountPriceCentUnit());
			newOrder.setArrivalTime(orderInput.getBeginDateTime());
			newOrder.setDepartTime(orderInput.getEndDateTime());
			newOrder.setNightCount(calendarCommon.getNightCount(orderInput.getBeginDateTime(), orderInput.getEndDateTime()));// 入住晚数
			newOrder.setSource(orderInput.getSource());// 来源
			newOrder.setCreateTime(now);
			newOrder.setExpireTime(DateUtil.addMinutes(now, Integer.parseInt(expire)));
			newOrder.setPayType(orderInput.getPayType());// 支付类型
			// 是否要发票
			newOrder.setOrderType(orderInput.getOrderType());// 订单类型
			newOrder.setPayDtm(now);// 不管是否未支付,默认先传当前的时间
			newOrder.setLastModDtm(now);
			newOrder.setOtaOrderNo(orderInput.getOtaOrderNo());// 为空就不会保存
			
			Long orderId = (Long) daoUtil.orderMgntDao.addAndGetPk(newOrder);
			newOrder.setOrderId(orderId.intValue());// 前台要用这信息

			// 订单扩展信息
			XbOrderExt orderExt = new XbOrderExt();
			orderExt.setOrderId(orderId.intValue());
			Long orderExtId = (Long) daoUtil.orderExtDao.addAndGetPk(orderExt);
			orderExt.setExtId(orderExtId.intValue());

			// 保存入住单
			List<NewCheckinOut> newCheckinList = new ArrayList<>();
			for (CheckinInput checkinInput : checkinInputList) {
				XbRoom roomInfo = daoUtil.roomMgntDao.findByPk(checkinInput.getRoomId());
				
				XbCheckin newCheckin = new XbCheckin();
				newCheckin.setOrderId(orderId.intValue());// 订单ID
				newCheckin.setLodgerId(checkinInput.getCheckinLodgerId());// 入住人ID
				newCheckin.setLodgerName(checkinInput.getCheckinName());
				newCheckin.setContactPhone(checkinInput.getCheckinMobile());
				newCheckin.setStat(AppConstants.Checkin_stat.NEW_0);// 未入住
				newCheckin.setCreateTime(now);
				newCheckin.setCheckinTime(checkinInput.getCheckinBeginTime());
				newCheckin.setCheckoutTime(checkinInput.getCheckinEndTime());
				newCheckin.setRoomId(checkinInput.getRoomId());
				newCheckin.setChkinPrice(checkinInput.getChkinPriceCentUnit());
				newCheckin.setNightCount(calendarCommon.getNightCount(checkinInput.getCheckinBeginTime(), checkinInput.getCheckinEndTime()));
				newCheckin.setLastModDtm(now);
				newCheckin.setOtaStayId(checkinInput.getOtaStayId());// 为空就不会保存
				newCheckin.setBatch(newCheckin.getOrderId() + "_" + newCheckin.getRoomId() + "_" + RandomUtil.getRandomStr(3));//生成批次,一张入住单只有一个批次,换房操作也会继续保留这个批次
				Long checkinId = (Long) daoUtil.checkinDao.addAndGetPk(newCheckin);
				newCheckin.setCheckinId(checkinId.intValue());

				// 入住单扩展信息
				XbCheckinExt checkinExt = new XbCheckinExt();
				checkinExt.setCheckinId(checkinId.intValue());
				Long checkinExtId = (Long) daoUtil.checkinExtDao.addAndGetPk(checkinExt);
				checkinExt.setExtId(checkinExtId.intValue());

				newCheckinList.add(new NewCheckinOut(newCheckin, checkinExt, roomInfo));
				
				// 保存到xb_checkiner表
				if (checkinInput.isSaveXbCheckiner()) {
					XbCheckiner checkiner = new XbCheckiner();
					checkiner.setCheckinId(checkinId.intValue());
					checkiner.setCreateDtm(DateUtil.getCurDateTime());
					checkiner.setIsMain(AppConstants.Checkiner_isMain.MAIN);
					checkiner.setLodgerId(checkinInput.getCheckinLodgerId());
					checkiner.setMobile(checkinInput.getCheckinMobile());
					checkiner.setName(checkinInput.getCheckinName());
					checkiner.setSendFlag(AppConstants.Checkiner_sendFlag.SEND_1);
					// 【注意】可以checkiner.setIdcardNo(idcardNo);// 当是去呼呼来的旧用户,可以从xb_lodger表里取出来更新进去。不过这里不写这个字段都行,办理入住的时候是一定会补身份证号的
					daoUtil.checkinerDao.addAndGetPk(checkiner);
				}
			}
			NewOrderOut newOrderOut = new NewOrderOut(newOrder, orderExt, newCheckinList);
			return newOrderOut;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	//
	/**
	 * 本地入住单入住(不涉及同步OTA以及更改开门密码,以及房态变更)
	 * 
	 * @param checkinId
	 * @return
	 */
	public int localCheckin(int checkinId, List<XbCheckiner> checkinerList) {
		try {
			XbCheckin newCheckin = new XbCheckin();
			newCheckin.setCheckinId(checkinId);
			newCheckin.setActualCheckinTime(DateUtil.getCurDateTime());
			newCheckin.setStat(AppConstants.Checkin_stat.CHECKIN_1);
			
			if (checkinerList != null && checkinerList.size() > 0) {
				for (XbCheckiner xbCheckiner : checkinerList) {
					daoUtil.checkinerDao.add(xbCheckiner);
				}
			}
			return daoUtil.checkinDao.updateEntityByPk(newCheckin);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 本地入住单离店(不涉及同步OTA,以及房态变更)
	 * 
	 * @param checkinId
	 * @param checkoutType,退房方式
	 * @return
	 */
	public int localCheckout(int checkinId, int checkoutType) {
		try {
			XbCheckin newCheckin = new XbCheckin();
			newCheckin.setCheckinId(checkinId);
			newCheckin.setActualCheckoutTime(DateUtil.getCurDateTime());
			newCheckin.setStat(AppConstants.Checkin_stat.CHECKOUT_2);
			newCheckin.setCheckoutType(checkoutType);
			return daoUtil.checkinDao.updateEntityByPk(newCheckin);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 本地取消(不涉及同步OTA,以及房态变更)<br>
	 * 
	 * @param orderId
	 * @return
	 */
	public int localCancel(int orderId) {
		try {
			XbOrder newOrder = new XbOrder();
			newOrder.setOrderId(orderId);
			newOrder.setStat(AppConstants.Order_stat.CANCEL_2);
			return daoUtil.orderMgntDao.updateEntityByPk(newOrder);
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}
}
