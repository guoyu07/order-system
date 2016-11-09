package cn.com.xbed.app.service.ordermodule.neworder;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.commons.util.AppConstants;
import cn.com.xbed.app.dao.common.DaoUtil;
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
public class QhhOrderImpl extends AbstractNewOrderTemplate {
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
	private DaoUtil daoUtil;
	@Resource
	private EventService eventService;

	@Override
	public NewOrderOut localNewOrder(OrderInput orderInput, List<CheckinInput> checkinInputList) {
		return localOrderModuleBase.localNewOrder(orderInput, checkinInputList);
	}

//	@Override
//	public OtaNewOrderOut otaNewOrder(int orderId) {
//		return null;// OTA同步过来不需要再同步给OTA
//	}

	@Override
	public void roomStateNewOrder(int orderId) {
		roomStateModuleBase.roomStateNewOrder(orderId);
	}

	@Override
	public int addInvoice(int orderId, Invoice invoiceInfo) {
		return 0;// OTA同步过来不需要发票
	}

	@Override
	public void maintain(int orderId, OtaNewOrderOut otaNewOrderOut) {
		// 订单状态需要改改
	}

	@Override
	public void throwEvent(int orderId) {
		// 手动控制比较灵活
//		// 新建已支付的时候就抛事件,否则不抛
//		XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
//		if (orderInfo.getStat() == AppConstants.Order_stat.PAYED_1) {
//			eventService.throwBookAfterPaySuccEvent(orderId);
//		}
	}
}
