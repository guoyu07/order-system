package cn.com.xbed.app.commons.enu;

public enum OperCodeOrder {
	NEW_ORDER, // 新建订单
	CANCEL_ORDER_NOT_CHECKIN, // 订单取消（未入住）
	CANCEL_ORDER_HAS_CHECKIN,// 订单取消（已入住）
	CANCEL_ORDER_UN_PAID,// 取消未支付
}