package cn.com.xbed.app.service.ordermodule.cancel;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.xbed.app.bean.XbOrder;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.common.DaoUtil;
import cn.com.xbed.app.service.OrderMgntCommon;
import cn.com.xbed.app.service.eventsystem.EventService;
import cn.com.xbed.app.service.localordermodule.LocalOrderModuleBase;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.intf.AbstractCancelTemplate;
import cn.com.xbed.app.service.otamodule.IOtaModuleBase;
import cn.com.xbed.app.service.roomstatemodule.RoomStateModuleBase;

@Service
@Transactional
public class AutoProcessCancelOutImpl extends AbstractCancelTemplate {
	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(AutoProcessCancelOutImpl.class));

	@Resource
	private LocalOrderModuleBase localOrderModuleBase;

	@Resource(name = "qhhOtaModuleBase")
	private IOtaModuleBase iOtaModuleBase;

	@Resource
	private RoomStateModuleBase roomStateModuleBase;
	@Resource
	private MaintainModuleBase maintainModuleBase;
	@Resource
	private EventService eventService;
	@Resource
	private OrderMgntCommon orderMgntCommon;
	@Resource
	private DaoUtil daoUtil;

	@Override
	public int localCancel(int orderId) {
		return localOrderModuleBase.localCancel(orderId);
	}

	@Override
	public void roomStateCancel(int orderId) {
		roomStateModuleBase.roomStateCancel(orderId);
	}

	@Override
	public void throwEvent(int orderId) {
		eventService.throwCancelUnpaidEvent(orderId);
	}

	@Override// 其实可以提取到抽象父类,因为这么几个子类全都是一样的实现逻辑
	public void operCouponCard(int orderId) {
		exceptionHandler.getLog().info("进程取消订单,可能需要取消卡券,orderId:" + orderId);
		XbOrder orderInfo = daoUtil.orderMgntDao.findByPk(orderId);
		orderMgntCommon.unLockCouponCard(orderInfo.getOrderNo(), orderInfo.getLodgerId());
	}

//	@Override
//	public void otaCancel(int orderId) {
//		iOtaModuleBase.otaCancel(orderId);
//	}

}
