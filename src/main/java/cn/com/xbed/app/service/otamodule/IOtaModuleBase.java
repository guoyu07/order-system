package cn.com.xbed.app.service.otamodule;

import cn.com.xbed.app.service.otamodule.vo.OtaNewOrderOut;

public interface IOtaModuleBase {

	OtaNewOrderOut otaNewOrder(int orderId);

	String otaCheckin(int checkinId);

	void otaCheckout(int checkinId);// 去呼呼办理退房

	void otaCancel(int orderId);// 取消去呼呼的单
}