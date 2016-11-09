package cn.com.xbed.app.service.syncin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.bean.XbQhhSyncLog;
import cn.com.xbed.app.bean.vo.WarningEntity;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.CommonService;
import cn.com.xbed.app.service.WarningSystemTool;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.invoicemodule.vo.Invoice;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.NewCheckinOut;
import cn.com.xbed.app.service.localordermodule.vo.NewOrderOut;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.impl.CancelType;
import cn.com.xbed.app.service.ordermodule.impl.CheckoutType;
import cn.com.xbed.app.service.ordermodule.impl.FromType;
import cn.com.xbed.app.service.ordermodule.impl.OrderUtil;
import cn.com.xbed.app.service.smsmodule.CommonSms;
import cn.com.xbed.app.service.syncin.vo.SyncOrdEntity;
import cn.com.xbed.app.service.syncin.vo.SyncRoomEntity;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.commons.util.DateUtil;
import cn.com.xbed.app.commons.util.JsonHelper;
import cn.com.xbed.app.commons.util.RandomUtil;

@Service
public class SyncInTool {
	@Resource
	private DaoUtil daoUtil;
	@Resource
	private CommonService commonService;
	@Resource
	private WarningSystemTool warningSystemTool;
	@Resource
	private CommonSms commonSms;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private OrderUtil orderUtil;
	@Resource
	private EventService eventService;
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(SyncInTool.class));

	// 同步未支付订单,建单
	@Transactional
	public void syncUnpaid(SyncOrdEntity syncOrdEntity) {
		try {
			// 1准备数据
			String contactMobile = syncOrdEntity.getContactMobile();
			String otaOrderNo = syncOrdEntity.getOrderNo();
			String orderStatusCode = syncOrdEntity.getOrderStatusCode();
			// 2判断是否能继续
			if (StringUtils.isBlank(orderStatusCode)
					|| Integer.parseInt(orderStatusCode) != AppConstants.Qunar_orderStat.NEW_1) {
				return;
			}
			XbOrder localOrderInfo = daoUtil.orderMgntDao.findByOtaOrderNo(otaOrderNo);
			if (localOrderInfo != null) {// 双重保险
				return;
			}
			// 3入库
			Map<String, Object> result = this.saveNotPaidOrderFromOta(syncOrdEntity);
			NewOrderOut out = (NewOrderOut) result.get("newOrderOut");

			// 4发短信(新建用户的短信已经发送,预订成功的短信需要支付后才发)
			
			exceptionHandler.getLog().warn("~~~~去呼呼同步1状态,成功~~~~~~");
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional
	public void transUnpaid2paid(int orderId) {
		XbOrder newOrder = new XbOrder();
		newOrder.setOrderId(orderId);
		newOrder.setStat(AppConstants.Order_stat.PAYED_1);
		newOrder.setPayDtm(DateUtil.getCurDateTime());
		newOrder.setPayType(AppConstants.Order_payType.UNKNOW_2);
		daoUtil.orderMgntDao.updateEntityByPk(newOrder);
	}

	// 同步已支付订单
	@Transactional
	public void syncPaid(SyncOrdEntity syncOrdEntity) {
		try {
			// 准备数据
			String contactMobile = syncOrdEntity.getContactMobile();
			String otaOrderNo = syncOrdEntity.getOrderNo();
			XbOrder localOrderInfo = daoUtil.orderMgntDao.findByOtaOrderNo(otaOrderNo);
			if (localOrderInfo == null) {// 说明某些原因错过了1的状态,那不应该错过2
				exceptionHandler.getLog().info("直接同步过来的就是2,跳过了1的状态");
				this.directSyncPaid(syncOrdEntity);
			} else if (localOrderInfo != null && localOrderInfo.getStat() == AppConstants.Order_stat.NEW_0) {
				// 改成已支付的状态
				int orderId = localOrderInfo.getOrderId();
				this.transUnpaid2paid(orderId);

				// 下发短信,维护xb_order_ext
				eventService.throwBookAfterPaySuccEvent(orderId);
				maintainModuleBase.markHasSendBookSms(orderId);
				exceptionHandler.getLog().warn("~~~~去呼呼同步2状态,成功~~~~~~");
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 同步已入住
	@Transactional
	public void syncCheckin(SyncOrdEntity syncOrdEntity) {
		try {
			String otaOrderNo = syncOrdEntity.getOrderNo();

			// 查看本地有没有单
			XbOrder localOrderInfo = daoUtil.orderMgntDao.findByOtaOrderNo(otaOrderNo);
			if (localOrderInfo != null) {
				List<XbCheckin> xbCheckinList = daoUtil.checkinDao.findByOrderId(localOrderInfo.getOrderId());
				if (!xbCheckinList.isEmpty()) {
					exceptionHandler.getLog().warn("$查询本入住单:" + xbCheckinList);
					boolean checkCanCheckin = true;
					for (XbCheckin xbCheckin : xbCheckinList) {
						if (xbCheckin.getStat() != AppConstants.Checkin_stat.NEW_0) {
							exceptionHandler.getLog().warn("$入住单状态不对xb_checkin.stat " + xbCheckin.getStat());
							checkCanCheckin = false;
							break;
						}
						if (localOrderInfo.getStat() != AppConstants.Order_stat.PAYED_1) {
							exceptionHandler.getLog().warn("$订单状态不对xb_order.stat " + localOrderInfo.getStat());
							checkCanCheckin = false;
							break;
						}
					}
					if (!checkCanCheckin) {
						return;
					}
					exceptionHandler.getLog().warn("去呼呼后台帮忙办理入住同步过来的(正常去呼呼的单也需要在我们渠道办理入住，这是异常情况，可能是通过去呼呼后台帮忙办理入住的)");
					for (XbCheckin xbCheckin : xbCheckinList) {
						// 保存主入住人
//						String customerName = xbCheckin.getLodgerName();
//						String customerTelNo = xbCheckin.getContactPhone();
//						XbCheckiner mainCheckiner = new XbCheckiner();
//						mainCheckiner.setCheckinId(xbCheckin.getCheckinId());
//						mainCheckiner.setCreateDtm(DateUtil.getCurDateTime());
//						mainCheckiner.setIdcardNo("");// 去呼呼同步过来,不清楚身份证
//						mainCheckiner.setLodgerId(xbCheckin.getLodgerId());
//						mainCheckiner.setMobile(customerTelNo);
//						mainCheckiner.setName(customerName);
//						mainCheckiner.setIsMain(AppConstants.Checkiner_isMain.MAIN);// 是主入住人
//						mainCheckiner.setRemark("去呼呼同步过来的办理入住,主住户");
//						mainCheckiner.setSendFlag(AppConstants.Checkiner_sendFlag.SEND_1);// 去呼呼已经发送了
//						List<XbCheckiner> checkinerList = new ArrayList<>();
//						checkinerList.add(mainCheckiner);

						orderUtil.checkin(xbCheckin.getCheckinId(), null);
					}
				}
			} else {
				// 帮忙创建已支付已入住的订单.
				// 由于同步问题,导致已支付的订单同步不过来,运营直接在去呼呼后台帮忙办理入住,在系统修复后,同步过来的订单变成了已入住.
				exceptionHandler.getLog().error("在入住阶段本地依然没有订单,补上(由于同步事故造成) begin");
				this.directSyncCheckin(syncOrdEntity);
				exceptionHandler.getLog().error("在入住阶段本地依然没有订单,补上(由于同步事故造成) end");
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 同步已退房(只会处理去呼呼的正常订单的退房,我们下的订单,包括房态控制单不同步过来)
	@Transactional
	public void syncCheckout(SyncOrdEntity syncOrdEntity) {
		try {
			String otaOrderNo = syncOrdEntity.getOrderNo();
			XbOrder localOrderInfo = daoUtil.orderMgntDao.findByOtaOrderNo(otaOrderNo);
			if (localOrderInfo != null) {
				List<XbCheckin> checkinList = daoUtil.checkinDao.findByOrderId(localOrderInfo.getOrderId());
				for (XbCheckin xbCheckin : checkinList) {
					if (xbCheckin.getStat() == AppConstants.Checkin_stat.CHECKIN_1) {
						orderUtil.checkout(xbCheckin.getCheckinId(),
								AppConstants.Checkin_checkoutType.QHH_SYNC_CHECKOUT_3, CheckoutType.AUTO);// 有通知丽家会
					}
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 同步已取消(只会同步去呼呼来的订单,我们的单和房态控制单不同步)
	@Transactional
	public void syncCancel(SyncOrdEntity syncOrdEntity) {
		try {
			String otaOrderNo = syncOrdEntity.getOrderNo();
			XbOrder localOrderInfo = daoUtil.orderMgntDao.findByOtaOrderNo(otaOrderNo);
			if (localOrderInfo != null) {
				if (localOrderInfo.getStat() != AppConstants.Order_stat.CANCEL_2) {
					orderUtil.cancel(localOrderInfo.getOrderId(), CancelType.CUST);
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	// 未经过同步未支付订单,直接就同步已支付的(防止去呼呼有这种情况发生)
	@Transactional
	public void directSyncPaid(SyncOrdEntity syncOrdEntity) {
		try {
			// 1准备数据
			String contactMobile = syncOrdEntity.getContactMobile();
			String otaOrderNo = syncOrdEntity.getOrderNo();

			// 2判断是否能继续
			XbOrder localOrderInfo = daoUtil.orderMgntDao.findByOtaOrderNo(otaOrderNo);
			if (localOrderInfo != null) {// 双重保险
				return;
			}
			// 3入库
			Map<String, Object> result = this.saveNotPaidOrderFromOta(syncOrdEntity);
			NewOrderOut out = (NewOrderOut) result.get("newOrderOut");

			// 4改成已支付
			int orderId = out.getOrderInfo().getOrderId();
			this.transUnpaid2paid(orderId);

			// 5发送短信给联系人,维护xb_order_ext
			eventService.throwBookAfterPaySuccEvent(orderId);
			maintainModuleBase.markHasSendBookSms(orderId);
			exceptionHandler.getLog().warn("~~~~去呼呼直接同步2状态,成功~~~~~~");
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	/**
	 * 内部调用,防止重复代码
	 * 
	 * @param syncOrdEntity
	 */
	@Transactional
	public Map<String, Object> saveNotPaidOrderFromOta(SyncOrdEntity syncOrdEntity) {
		Map<String, Object> result = new HashMap<>();
		try {
			// 1准备数据
			String contactMobile = syncOrdEntity.getContactMobile();
			String contactName = syncOrdEntity.getContactName();
			String otaOrderNo = syncOrdEntity.getOrderNo();
			String arrivalTimeStr = syncOrdEntity.getCheckInTime();
			String departTimeStr = syncOrdEntity.getCheckOutTime();
			String totalPriceYuanUnit = syncOrdEntity.getRentMoney();
			Date arrivalTime = DateUtil.parseDateTimeNoSec(arrivalTimeStr);
			Date departTime = DateUtil.parseDateTimeNoSec(departTimeStr);

			// 2 入库
			// 2.1是否要创建下单人
			String randomPass = RandomUtil.getRandomStr(6);
			Map<String, Object> lodgerMap = commonService.judgeIfNeedCreateLodger(contactMobile, contactName,
					"去呼呼同步自动创建下单人", AppConstants.Lodger_source.OTA_2, randomPass,
					AppConstants.Lodger_hasChgPwd.NOT_YET_1, true);
			int contactLodgerId = (int) lodgerMap.get("lodgerId");// 联系人
			boolean isNewCreate = (boolean) lodgerMap.get("isNewCreate");

			result.put("contactLodgerId", contactLodgerId);
			result.put("isNewCreate", isNewCreate);
			result.put("randomPass", randomPass);

			// 3.3保存订单
			int stat = AppConstants.Order_stat.NEW_0;
			int source = AppConstants.Order_source.QUNAR_1;
			int payType = AppConstants.Order_payType.UNKNOW_2;
			int orderType = AppConstants.Order_orderType.CUST_ORDER_1;
			int totalPriceCentUnit = Integer.parseInt(totalPriceYuanUnit) * 100;
			int bookLodgerId = contactLodgerId;
			String bookLodgerName = contactName;
			String bookLodgerMobile = contactMobile;
			Date beginDateTime = arrivalTime;
			Date endDateTime = departTime;
			OrderInput orderInput = new OrderInput(stat, source, payType, orderType, totalPriceCentUnit, bookLodgerId,
					bookLodgerName, bookLodgerMobile, beginDateTime, endDateTime, otaOrderNo, 0);
			List<CheckinInput> checkinInputList = new ArrayList<>();
			List<SyncRoomEntity> syncRoomInfoList = syncOrdEntity.getRoomInfos();
			for (SyncRoomEntity syncRoomEntity : syncRoomInfoList) {
				int roomId = Integer.parseInt(syncRoomEntity.getRoomId());
				int checkinLodgerId = contactLodgerId;// 入住人跟下单人一样
				String checkinName = syncRoomEntity.getCustomerName();
				String checkinMobile = syncRoomEntity.getCustomerTelNo();
				String chkinTimeStr = syncRoomEntity.getChkinTime();
				String chkoutTimeStr = syncRoomEntity.getChkoutTime();
				Date checkinBeginTime = DateUtil.parseDateTimeNoSec(chkinTimeStr);
				Date checkinEndTime = DateUtil.parseDateTimeNoSec(chkoutTimeStr);
				String roomPriceYuanUnit = syncRoomEntity.getRoomPrice();
				int chkinPriceCentUnit = Integer.parseInt(roomPriceYuanUnit) * 100;
				String otaStayId = syncRoomEntity.getStayID();
				CheckinInput checkinInput = new CheckinInput(roomId, checkinLodgerId, checkinName, checkinMobile,
						checkinBeginTime, checkinEndTime, chkinPriceCentUnit, otaStayId);
				checkinInputList.add(checkinInput);
			}
			Invoice invoiceInfo = null;// 无需发票
			FromType fromType = FromType.FROM_OTA;
			NewOrderOut out = orderUtil.newOrder(orderInput, checkinInputList, invoiceInfo, fromType);

			result.put("newOrderOut", out);
			return result;
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional
	public void trans2checkin(int checkinId) {
		XbCheckin xbCheckin = new XbCheckin();
		xbCheckin.setCheckinId(checkinId);
		xbCheckin.setActualCheckinTime(DateUtil.getCurDateTime());
		xbCheckin.setStat(AppConstants.Checkin_stat.CHECKIN_1);
		xbCheckin.setOpenPwd("人工补救");
		daoUtil.checkinDao.updateEntityByPk(xbCheckin);
		
		orderUtil.checkin(checkinId, null);
	}

	// 在入住阶段本地依然没有订单,补上(由于同步事故造成)
	@Transactional
	public void directSyncCheckin(SyncOrdEntity syncOrdEntity) {
		try {
			// 准备数据
			String contactMobile = syncOrdEntity.getContactMobile();

			// 保存订单
			Map<String, Object> result = this.saveNotPaidOrderFromOta(syncOrdEntity);
			NewOrderOut out = (NewOrderOut) result.get("newOrderOut");

			// 4改成已支付
			int orderId = out.getOrderInfo().getOrderId();
			this.transUnpaid2paid(orderId);

			// 发送短信给联系人
			String sw = daoUtil.sysParamDao.getValueByParamKeyNoThrow("switch.send_sms_fix_checkin_order");
			if (sw != null && sw.equalsIgnoreCase("true")) {
				// 抛事件,维护xb_order_ext
				eventService.throwBookAfterPaySuccEvent(orderId);
				maintainModuleBase.markHasSendBookSms(orderId);
			}

			// 改成已入住
			for (NewCheckinOut newCheckinOut : out.getNewCheckinList()) {
				this.trans2checkin(newCheckinOut.getCheckinInfo().getCheckinId());
			}

			// 维护xb_checkiner表
			// 由orderUtil.newOrder保存了
			/*for (NewCheckinOut newCheckinOut : out.getNewCheckinList()) {
				XbCheckin checkinInfo = newCheckinOut.getCheckinInfo();
				int checkinId = checkinInfo.getCheckinId();
				XbCheckiner checkiner = new XbCheckiner();
				checkiner.setCheckinId(checkinId);
				checkiner.setCreateDtm(DateUtil.getCurDateTime());
				checkiner.setLodgerId(checkinInfo.getLodgerId());
				checkiner.setMobile(contactMobile);
				checkiner.setName(checkinInfo.getLodgerName());
				checkiner.setIsMain(AppConstants.Checkiner_isMain.MAIN);// 主入住人
				checkiner.setSendFlag(AppConstants.Checkiner_sendFlag.SEND_1);
				checkiner.setRemark("OTA同步,订单阶段创建");
				daoUtil.checkinerDao.addAndGetPk(checkiner);
			}*/
			exceptionHandler.getLog().warn("~~~~去呼呼同步3状态,成功~~~~~~");
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logSyncError(String errorMsg, Exception e, SyncOrdEntity syncOrdEntity) {
		try {
			exceptionHandler.getLog().error(errorMsg, e);
			String otaOrderNo = syncOrdEntity.getOrderNo();
			String orderStatusCode = syncOrdEntity.getOrderStatusCode();
			if (!this.existQhhSyncLog(otaOrderNo, orderStatusCode)) {
				this.logWarningSystem(otaOrderNo, orderStatusCode, this.getWarningEntity(errorMsg));
				this.addFailQhhSyncLog(syncOrdEntity, errorMsg);
			}
		} catch (Exception ee) {
			exceptionHandler.getLog().error("logSyncSuccess错误 ", ee);// 不要抛出异常！！！
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logSyncSuccess(SyncOrdEntity syncOrdEntity) {
		try {
			this.addSuccessQhhSyncLog(syncOrdEntity);
		} catch (Exception e) {
			exceptionHandler.getLog().error("logSyncSuccess错误 ", e);// 不要抛出异常！！！
		}
	}

	@Transactional
	public boolean existQhhSyncLog(String otaOrderNo, String orderStatusCode) {
		try {
			String sql = "SELECT COUNT(*) logSize FROM xb_qhh_sync_log WHERE ota_order_no=? AND order_status_code=?";
			List<Map<String, Object>> mapList = daoUtil.sysParamDao.queryMapList(sql,
					new Object[] { otaOrderNo, orderStatusCode });
			if (!mapList.isEmpty()) {
				long l = (long) mapList.get(0).get("logSize");
				if (l > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			throw exceptionHandler.logServiceException(e);
		}
		return false;
	}

	/**
	 * 记录日志到预警系统<br>
	 * 
	 * 不要抛出异常！！！
	 * 
	 * @param otaOrderNo
	 * @param orderStatusCode
	 * @param warningEntity
	 */
	@Transactional
	public void logWarningSystem(String otaOrderNo, String orderStatusCode, WarningEntity warningEntity) {
		try {
			warningSystemTool.warningLogMultiThread(warningEntity);
		} catch (Exception e) {
			exceptionHandler.getLog().error("logWarningSystem 错误" + otaOrderNo + "," + orderStatusCode);// 不要抛出异常！！！
		}
	}

	private WarningEntity getWarningEntity(String errorMsg) {
		WarningEntity warningEntity = new WarningEntity();
		warningEntity.setLogType(4);
		warningEntity.setLogLevel(4);
		warningEntity.setCreateTime(DateUtil.getCurDateTime());
		warningEntity.setLogContent(errorMsg);
		warningEntity.setConfigItemCode("ORD_SYS.QHH_SYNC");
		return warningEntity;
	}

	/**
	 * 增加成功的日志到xb_qhh_sync_log表<br>
	 * 
	 * 不要抛出异常！！！
	 * 
	 * @param syncOrder
	 */
	@Transactional
	public void addSuccessQhhSyncLog(SyncOrdEntity syncOrder) {
		try {
			String contactMobile = syncOrder.getContactMobile();
			String contactName = syncOrder.getContactName();
			String otaOrderNo = syncOrder.getOrderNo();
			String orderStatusCode = syncOrder.getOrderStatusCode();
			XbQhhSyncLog entity = new XbQhhSyncLog();
			entity.setCreateDtm(DateUtil.getCurDateTime());
			entity.setDatagram(JsonHelper.toJSONString(syncOrder));
			entity.setSucc(AppConstants.QhhSyncLog_succ.SUCC_1);
			entity.setOtaOrderNo(otaOrderNo);
			entity.setOrderStatusCode(orderStatusCode);
			entity.setOrderMan(contactName);
			entity.setOrderMobile(contactMobile);
			daoUtil.qhhSyncLogDao.add(entity);
		} catch (Exception e) {
			exceptionHandler.getLog().error("addSuccessQhhSyncLog错误 ", e);// 不要抛出异常！！！
		}
	}

	/**
	 * 增加错误日志到xb_qhh_sync_log表<br>
	 * 
	 * 不要抛出异常！！！
	 * 
	 * @param syncOrder
	 * @param failMsg
	 */
	@Transactional
	public void addFailQhhSyncLog(SyncOrdEntity syncOrdEntity, String failMsg) {
		try {
			String contactMobile = syncOrdEntity.getContactMobile();
			String contactName = syncOrdEntity.getContactName();
			String otaOrderNo = syncOrdEntity.getOrderNo();
			String orderStatusCode = syncOrdEntity.getOrderStatusCode();
			XbQhhSyncLog entity = new XbQhhSyncLog();
			entity.setCreateDtm(DateUtil.getCurDateTime());
			entity.setDatagram(JsonHelper.toJSONString(syncOrdEntity));
			entity.setSucc(AppConstants.QhhSyncLog_succ.FAIL_0);
			entity.setOtaOrderNo(otaOrderNo);
			entity.setOrderStatusCode(orderStatusCode);
			entity.setOrderMan(contactName);
			entity.setOrderMobile(contactMobile);
			entity.setFailMsg(failMsg);
			daoUtil.qhhSyncLogDao.add(entity);
		} catch (Exception e) {
			exceptionHandler.getLog().error("addFailQhhSyncLog错误 ", e);// 不要抛出异常！！！
		}
	}
}
