package cn.com.xbed.app.service.eventsystem;

public class EventCode {
	/**
	 * 退房或在住申请打扫,有房间需要清洁
	 */
	public static final String SOME_ROOM_NEED_TO_CLEAN = "ordersystem.someRoomNeedToClean";

	/**
	 * 未支付订单警告
	 */
	public static final String WARNING_UNPAID_20_MIN = "ordersystem.warningUnpaid20Min";

	/**
	 * 未支付订单取消
	 */
	public static final String CANCEL_UNPAID_30_MIN = "ordersystem.cancelUnpaid30Min";

	/**
	 * 当天10点钟提醒预退房
	 */
	public static final String PRE_CHECKOUT_HINT = "ordersystem.preCheckoutHint";

	/**
	 * 成功支付后
	 */
	public static final String BOOK_AFTER_PAY_SUCC = "ordersystem.bookAfterPaySucc";

	/**
	 * 用户主动取消订单
	 */
	public static final String CUST_CANCEL_ORDER = "ordersystem.custCancelOrder";

	/**
	 * 办理入住
	 */
	public static final String CHECKIN_HINT = "ordersystem.checkinHint";

	/**
	 * 主动办理退房
	 */
	public static final String ACTIVE_CHECKOUT_HINT = "ordersystem.activeCheckoutHint";

	/**
	 * 进程自动退房
	 */
	public static final String AUTO_CHECKOUT_HINT = "ordersystem.autoCheckoutHint";
	
	/**
	 * 续住退房
	 */
	public static final String OVERSTAY_CHECKOUT = "ordersystem.overstayCheckout";

	
	/**
	 * 换房
	 */
	public static final String CHG_ROOM = "ordersystem.chgRoom";
	
	
	/**
	 * OMS取消已支付
	 */
	public static final String OMS_CANCEL_PAID = "ordersystem.omsCancelPaid";
	
	
	/**
	 * OMS取消未支付
	 */
	public static final String OMS_CANCEL_UNPAID = "ordersystem.omsCancelUnpaid";
	
	/**
	 * 帮忙注册帐号的时间
	 */
	public static final String REGISTER_USER = "ordersystem.registerUser";
	
	/**
	 * 帮忙注册业主
	 */
	public static final String REGISTER_OWNER = "ordersystem.registerOwner";
	
	/**
	 * 业主下单
	 */
	public static final String OWNER_BOOK = "ordersystem.ownerBook";
	
	/**
	 * 在住申请打扫
	 */
	public static final String APPLY_CLEAN = "ordersystem.applyClean";
	
	/**
	 * 退房通知运营人员
	 */
	public static final String CHECKOUT_NOTIFY = "ordersystem.checkoutNotify";
	
	
	/**
	 * 重发开门密码
	 */
	public static final String RESEND_OPEN_PWD = "ordersystem.resendOpenPwd";
	
	
	public static class LogEventThrow_logType {
		// 类型:1-20分钟警告 2-30分钟取消 3-预退房 4-预订并支付成功 5-用户主动取消订单 6-办理入住 7-办理退房(主动)
		// 8进程办理退房(被动) 9-注册了用户 10-取消已支付  11-取消未支付 12-注册普通用户 13-注册业主 14-业主下单
		// 15-在住申请打扫   16-退房通知运营打扫  17-重发开门密码 18-续住换房
		public static final int WARNING_UNPAID_20_MIN_1 = 1;
		public static final int CANCEL_UNPAID_30_MIN_2 = 2;
		public static final int PRE_CHECKOUT_HINT_3 = 3;
		public static final int BOOK_AFTER_PAY_SUCC = 4;
		public static final int CUST_CANCEL_ORDER = 5;
		public static final int CHECKIN_HINT = 6;
		public static final int ACTIVE_CHECKOUT_HINT = 7;
		public static final int AUTO_CHECKOUT_HINT = 8;
		public static final int CHG_ROOM = 9;
		public static final int OMS_CANCEL_PAID = 10;
		public static final int OMS_CANCEL_UNPAID = 11;
		public static final int REGISTER_USER = 12;
		public static final int REGISTER_OWNER = 13;
		public static final int OWNER_BOOK = 14;
		public static final int APPLY_CLEAN = 15;
		public static final int CHECKOUT_NOTIFY = 16;
		public static final int RESEND_OPEN_PWD = 17;
		public static final int OVERSTAY_CHECKOUT = 18;
	}
}
