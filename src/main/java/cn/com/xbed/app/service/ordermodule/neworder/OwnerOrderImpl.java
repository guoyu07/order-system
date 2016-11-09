package cn.com.xbed.app.service.ordermodule.neworder;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.invoicemodule.InvoiceModuleBase;
import cn.com.xbed.app.service.invoicemodule.vo.Invoice;
import cn.com.xbed.app.service.localordermodule.LocalOrderModuleBase;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.NewOrderOut;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.intf.AbstractNewOrderTemplate;
import cn.com.xbed.app.service.otamodule.IOtaModuleBase;
import cn.com.xbed.app.service.otamodule.vo.OtaNewOrderOut;
import cn.com.xbed.app.service.roomstatemodule.RoomStateModuleBase;

@Service
@Transactional
public class OwnerOrderImpl extends AbstractNewOrderTemplate {
	@Resource
	private LocalOrderModuleBase localOrderModuleBase;
	@Resource
	private InvoiceModuleBase invoiceModuleBase;

	@Resource(name = "qhhOtaModuleBase")
	private IOtaModuleBase iOtaModuleBase;

	@Resource
	private RoomStateModuleBase roomStateModuleBase;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	
	@Resource
	private EventService eventService;

	@Override
	public NewOrderOut localNewOrder(OrderInput orderInput, List<CheckinInput> checkinInputList) {
		return localOrderModuleBase.localNewOrder(orderInput, checkinInputList);
	}

//	@Override
//	public OtaNewOrderOut otaNewOrder(int orderId) {
//		return iOtaModuleBase.otaNewOrder(orderId);
//	}

	@Override
	public void roomStateNewOrder(int orderId) {
		roomStateModuleBase.roomStateNewOrder(orderId);
	}

	@Override
	public int addInvoice(int orderId, Invoice invoiceInfo) {
		return invoiceModuleBase.addInvoice(orderId, invoiceInfo);
	}

	@Override
	public void maintain(int orderId, OtaNewOrderOut otaNewOrderOut) {
		maintainModuleBase.maintain(orderId, otaNewOrderOut);
	}

	@Override
	public void throwEvent(int orderId) {
		// 业主下单,一下就是已支付的
		eventService.throwOwnerBookEvent(orderId);
	}
}
