package cn.com.xbed.app.service.ordermodule.intf;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.service.invoicemodule.vo.Invoice;
import cn.com.xbed.app.service.localordermodule.vo.CheckinInput;
import cn.com.xbed.app.service.localordermodule.vo.NewOrderOut;
import cn.com.xbed.app.service.localordermodule.vo.OrderInput;
import cn.com.xbed.app.service.otamodule.vo.OtaNewOrderOut;

@Service
@Transactional
public abstract class AbstractNewOrderTemplate {

	//
	public NewOrderOut newOrder(OrderInput orderInput, List<CheckinInput> checkinInputList, Invoice invoiceInfo) {
		// 1.本地创建订单
		NewOrderOut newOrderOut = localNewOrder(orderInput, checkinInputList);

		// 2.同步OTA【改成房态系统去同步】
		//OtaNewOrderOut otaNewOrderOut = otaNewOrder(orderId);


		// 3.是否要发票
		int orderId = newOrderOut.getOrderInfo().getOrderId();
		addInvoice(orderId, invoiceInfo);

		// 4.维护一些字段(例如ota订单号,ota入住单号(后改由Bonly系统维护))
		OtaNewOrderOut otaNewOrderOut = null;
		maintain(orderId, otaNewOrderOut);
		
		// 5.锁定房间
		try {
			roomStateNewOrder(orderId);
		} catch (Exception e) {
			throw new RuntimeException("房态系统报错,新建:", e);
		}
		
		// 6.抛出事件
		throwEvent(orderId);

		// 7.扩展方法(钩子)
		hook(orderId);
		
		return newOrderOut;
	}

	public abstract NewOrderOut localNewOrder(OrderInput orderInput, List<CheckinInput> checkinInputList);

	//public abstract OtaNewOrderOut otaNewOrder(int orderId);

	public abstract void roomStateNewOrder(int orderId);

	public abstract int addInvoice(int orderId, Invoice invoiceInfo);

	public abstract void maintain(int orderId, OtaNewOrderOut otaNewOrderOut);

	public abstract void throwEvent(int orderId);
	
	public Object hook(int orderId) {
		return null;
	}

}
