package cn.com.xbed.app.service.ordermodule.intf;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public abstract class AbstractCheckoutTemplate {

	//
	public void checkout(int checkinId, int checkoutType) {
		// 1.本地进行checkout
		localCheckout(checkinId, checkoutType);

		// 2.ota 办理退房【改成房态系统去同步】
		//otaCheckout(checkinId);
		
		// 3.修改房态房间
		try {
			roomStateCheckout(checkinId);
		} catch (Exception e) {
			throw new RuntimeException("房态系统报错,退房:", e);
		}

		// 4.抛出事件
		throwEvent(checkinId);
		
		// 5. 通知丽家会(发送清洁短信给运营人员等)
		notifyCleanSystem(checkinId, checkoutType);
		
		// 6.扩展方法(钩子)
		hook(checkinId);
	}

	public abstract int localCheckout(int checkinId, int checkoutType);

	//public abstract void otaCheckout(int checkinId);

	public abstract void roomStateCheckout(int checkinId);

	public abstract void notifyCleanSystem(int checkinId, int checkoutType);

	public abstract void throwEvent(int checkinId);

	public Object hook(int checkinId) {
		return null;
	}
}
