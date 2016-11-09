package cn.com.xbed.app.service.ordermodule.intf;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public abstract class AbstractCancelTemplate {

	public void cancel(int orderId) {
		// 1.本地进行cancel
		localCancel(orderId);

		// 2.ota 办理取消【改成房态系统去同步】
		//otaCancel(orderId);

		// 3.修改房态房间
		try {
			roomStateCancel(orderId);
		} catch (Exception e) {
			throw new RuntimeException("房态系统报错,取消:", e);
		}
		
		// 4.卡券相关操作(如果有的话)
		operCouponCard(orderId);
		
		// 4.抛事件
		throwEvent(orderId);
		
		// 5.扩展方法(钩子)
		hook(orderId);
	}

	public abstract int localCancel(int orderId);

	public abstract void roomStateCancel(int orderId);

	public abstract void operCouponCard(int orderId);
	
	//public abstract void otaCancel(int orderId);

	public abstract void throwEvent(int orderId);
	
	public Object hook(int orderId) {
		return null;
	}
}
