package cn.com.xbed.app.service.ordermodule.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.service.invoicemodule.vo.Invoice;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.NewOrderOut;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;
import cn.com.xbed.app.service.ordermodule.intf.AbstractCancelTemplate;
import cn.com.xbed.app.service.ordermodule.intf.AbstractCheckinTemplate;
import cn.com.xbed.app.service.ordermodule.intf.AbstractCheckoutTemplate;
import cn.com.xbed.app.service.ordermodule.intf.AbstractNewOrderTemplate;

@Service
@Transactional
public class OrderUtil {
	
	/**
	 * 新单
	 */
	@Resource(name = "qhhOrderImpl")
	private AbstractNewOrderTemplate qhhOrderImpl;// 新建,从去呼呼到本地(不需要发票)

	@Resource(name = "localOrderImpl")
	private AbstractNewOrderTemplate localOrderImpl;// 新建,从本地到去呼呼(可能需要发票)
	
	@Resource(name = "promoOrderImpl")
	private AbstractNewOrderTemplate promoOrderImpl;// 新建活动订单
	
	@Resource(name = "otaChannelOrderImpl")
	private AbstractNewOrderTemplate otaChannelOrderImpl;// 新建携程订单
	
	@Resource(name = "ownerOrderImpl")
	private AbstractNewOrderTemplate ownerOrderImpl;// 业主APP

	
	/**
	 * 办理入住
	 */
	@Resource(name = "checkinOutImpl")
	private AbstractCheckinTemplate checkinOutImpl;// 办理入住

	
	/**
	 * 退房
	 */
	@Resource(name = "autoCheckoutOutImpl")
	private AbstractCheckoutTemplate autoCheckoutOutImpl;// 退房,自动退
	
	@Resource(name = "activeCheckoutOutImpl")
	private AbstractCheckoutTemplate activeCheckoutOutImpl;// 退房,主动退
	
	@Resource(name = "chgRoomCheckoutOutImpl")
	private AbstractCheckoutTemplate chgRoomCheckoutOutImpl;// 退房,因为换房导致的退房(原单是已入住),不需要抛事件
	
	
	/**
	 * 取消
	 */
	@Resource(name = "custCancelOutImpl")
	private AbstractCancelTemplate custCancelOutImpl;// 取消,客户主动取消
	
	@Resource(name = "omsCancelPaidImpl")
	private AbstractCancelTemplate omsCancelPaidImpl;// 取消,OMS取消已支付
	
	@Resource(name = "omsCancelUnpaidImpl")
	private AbstractCancelTemplate omsCancelUnpaidImpl;// 取消,OMS取消未支付
	
	@Resource(name = "autoProcessCancelOutImpl")
	private AbstractCancelTemplate autoProcessCancelOutImpl;// 取消,自动进程超时取消
	
	
	

	public NewOrderOut newOrder(OrderInput orderInput, List<CheckinInput> checkinInputList, Invoice invoiceInfo, FromType fromType) {
		switch (fromType) {
		case FROM_LOCAL:
			return localOrderImpl.newOrder(orderInput, checkinInputList, invoiceInfo);
		case FROM_OTA:
			return qhhOrderImpl.newOrder(orderInput, checkinInputList, invoiceInfo);
		case OTA_CHANNEL:
			return otaChannelOrderImpl.newOrder(orderInput, checkinInputList, invoiceInfo);
		case PROMO:
			return promoOrderImpl.newOrder(orderInput, checkinInputList, invoiceInfo);
		case ONWER:
			return ownerOrderImpl.newOrder(orderInput, checkinInputList, invoiceInfo);
		default:
			throw new RuntimeException("类型错误");
		}
	}

	public String checkin(int checkinId, List<XbCheckiner> checkinerList) {
		return checkinOutImpl.checkin(checkinId, checkinerList);
	}

	public void checkout(int checkinId, int checkoutType, CheckoutType cType) {
		switch (cType) {
		case ACTIVE:
			activeCheckoutOutImpl.checkout(checkinId, checkoutType);
			break;
		case AUTO:
			autoCheckoutOutImpl.checkout(checkinId, checkoutType);
			break;
		case CHG_ROOM:
			chgRoomCheckoutOutImpl.checkout(checkinId, checkoutType);
			break;
		default:
			throw new RuntimeException("类型错误");
		}
	}

	public void cancel(int orderId, CancelType cancelType) {
		switch (cancelType) {
		case CUST:
			custCancelOutImpl.cancel(orderId);
			break;
		case OMS_PAID:
			omsCancelPaidImpl.cancel(orderId);
			break;
		case OMS_UNPAID:
			omsCancelUnpaidImpl.cancel(orderId);
			break;
		case AUTO_PROCESS:
			autoProcessCancelOutImpl.cancel(orderId);
			break;
		default:
			throw new RuntimeException("类型错误");
		}
		
	}
}