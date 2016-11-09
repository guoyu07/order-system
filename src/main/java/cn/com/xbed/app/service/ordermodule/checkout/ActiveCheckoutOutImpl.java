package cn.com.xbed.app.service.ordermodule.checkout;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.service.OrderMgntCommon;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.ljh.CleanSystemService;
import cn.com.xbed.app.service.localordermodule.LocalOrderModuleBase;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.intf.AbstractCheckoutTemplate;
import cn.com.xbed.app.service.otamodule.IOtaModuleBase;
import cn.com.xbed.app.service.roomstatemodule.RoomStateModuleBase;
import cn.com.xbed.app.commons.exception.ExceptionHandler;

@Service
@Transactional
public class ActiveCheckoutOutImpl extends AbstractCheckoutTemplate {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(ActiveCheckoutOutImpl.class));

	@Resource
	private LocalOrderModuleBase localOrderModuleBase;

	@Resource(name = "qhhOtaModuleBase")
	private IOtaModuleBase iOtaModuleBase;

	@Resource
	private RoomStateModuleBase roomStateModuleBase;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private CleanSystemService cleanSystemService;
	@Resource
	private EventService eventService;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	
	@Override
	public int localCheckout(int checkinId, int checkoutType) {
		return localOrderModuleBase.localCheckout(checkinId, checkoutType);
	}

	@Override
	public void roomStateCheckout(int checkinId) {
		roomStateModuleBase.roomStateCheckout(checkinId);
	}

//	@Override
//	public void otaCheckout(int checkinId) {
//		iOtaModuleBase.otaCheckout(checkinId);
//	}

	@Override
	public void notifyCleanSystem(int checkinId, int checkoutType) {
		// 因为已经限定了主动退房是必须没有续住单,所以这里就免去判断
		exceptionHandler.getLog().info("【外系统交互·跟丽家会】发送清洁单,checkinId:" + checkinId + ",checkoutType:" + checkoutType);
		cleanSystemService.sendCheckoutCleanSheet(checkinId);
	}

	@Override
	public void throwEvent(int checkinId) {
		// 因为已经限定了主动退房是必须没有续住单,所以这里就免去判断
		eventService.throwCheckoutNotifyEvent(checkinId);
		eventService.throwActiveCheckoutHintEvent(checkinId);
	}

}
