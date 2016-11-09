package cn.com.xbed.app.commons.util;

public class AppConstants {
	public static final String JSON_KEY = "json_data";
	
	public enum Location_locLevel {
		PROVINCE, CITY, DISTRICT,
	}
	
	public static class Checkin_precheckoutFlag {
		// 进程处理预离标记,0-未处理 1-已处理
		public static final int NOT_HANDLE_0 = 0;
		public static final int HANDLED_1 = 1;
	}
	
	public static class LogCouponCard_logType {
		// 操作类型 1锁定 2解锁 3核销
		public static final int CARD_LOCK_1 = 1;
		public static final int CARD_UNLOCK_2 = 2;
		public static final int CARD_DESTROY_3 = 3;
	}
	
	public static class LogOwnerCreate_isNewCreate {
		// 是否新建 0-否 1-是
		public static final int NO_0 = 0;
		public static final int YES_1 = 1;
	}
	
	public static class Lodger_lodgerType {
		// 用户类型 0-普通 1-业主
		public static final int NORMAL_0 = 0;
		public static final int ONWER_1 = 1;
	}
	
	public static class LogStopSheet_operType {
		// 操作类型 1-新建 2-取消
		public static final int NEW_1 = 1;
		public static final int CANCEL_2 = 2;
	}
	
	public static class OrderStop_stopStat {
		// 停用单状态 0新建 1开始 2结束 3停用取消  4-未开始取消
		public static final int NEW_0 = 0;
		public static final int BEGIN_1 = 1;
		public static final int END_2 = 2;
		public static final int HAS_BEGAN_CANCEL_3 = 3;
		public static final int NOT_BEGIN_CANCEL_4 = 4;
	}
	
	
	public static class XMonitor {
		public static final String QUEUE_KEY_CLEAN_DONE = "queue.cleanDoneAdvanced";

	}
	
	// 丽家会系统
	public static class CleanSystem {
		public static final String QUEUE_KEY_CLEAN_SHEET = "queue.cleanSheet";
		public static final String CREATE_NAME = "订单系统";

		public static class CleanSource {
			// 1订单系统、2定时系统、3客服
			public static final int ORDER_SYSTEM_1 = 1;
			public static final int CLOCK_SYSTEM_2 = 2;
			public static final int OMS_SYSTEM_3 = 3;
		}

		public static class Plan {
			// 1：在住清洁申请；2：退房；3：停用房清洁
			public static final int APPLY_CLEAN_1 = 1;
			public static final int CHECKOUT_2 = 2;
			public static final int STOP_3 = 3;
		}
	}

	public static class RoomStateOperCode {
		public static final int NEW_ORDER = 1010001; // 新订单
		public static final int CHECKIN = 1020001; // 办理入住
		public static final int CHECKOUT = 1040001; // 订单退房
		public static final int OVERSTAY_CHECKOUT = 1040002; // 续住退房
		public static final int CANCEL_ORDER_NOT_CHECKIN = 1030001; // 订单取消(未住 )
		public static final int CANCEL_ORDER_HAS_CHECKIN = 1030002; // 订单取消(已住 )
		public static final int NEW_FIX = 1060001; // 新维修单
		public static final int FIX_CANCEL = 1060004 ; // 取消维修(未开始) 
		public static final int FIX_BEGIN = 1060002; // 维修开始
		public static final int FIX_END = 1060003; // 维修结束
		public static final int NEW_STOP = 1050001; // 新停用单
		public static final int STOP_CANCEL = 1050004; // 取消停用(未开始)
		public static final int STOP_BEGIN = 1050002; // 停用开始
		public static final int STOP_END = 1050003; // 停用结束
		public static final int DIRTY_ROOM = 1070001; // 脏房
		public static final int CHECK_DONE = 1080001; // 核查完成
		public static final int CANCEL_ORDER_UN_PAID = 1030003; // 取消未支付订单
	}
	
	
	public static class QhhSyncLog_succ {
		// 是否成功 0-失败,1-成功
		public static final int FAIL_0 = 0;
		public static final int SUCC_1 = 1;
	}
	
	public static class Checkin_chgRoomFlag {
		// 换房标记,0-非换房单,1-原房间,2-新房间(怎么解决换后再换)
		public static final int NOMAL_0 = 0;
		public static final int ORI_ROOM_1 = 1;
		public static final int NEW_ROOM_2 = 2;
	}
	
	public static class Checkin_overstayFlag {
		// 续住标记,0-未续住,1-续住原单,2-续住新单
		public static final int NOMAL_0 = 0;
		public static final int ORI_ROOM_1 = 1;
		public static final int NEW_ROOM_2 = 2;
	}
	
	public static class Lodger_hasChgPwd {
		// 1-未改过 2-改过
		public static final int NOT_YET_1 = 1;
		public static final int HAS_CHG_2 = 2;
	}

	public static class RoomCtrlSheet_ctrlType {
		// 操作类型,1-禁售单
		public static final int SALE_FORBID_1 = 1;
	}

	public static class OrderOper_operType {
		// 操作类型,1-自己取消未支付订单,2-管理员取消未支付订单,3-管理员取消已支付订单
		public static final int SELF_CANCEL_1 = 1;
		public static final int ADMIN_CANCEL_NOT_PAID_2 = 2;
		public static final int ADMIN_CANCEL_PAID_3 = 3;
	}

	public static class OrderOper_isRefund {
		// 是否已经退款:1-无需退款,2-未退款,3-已退款,无需退款的忽略
		public static final int NO_REFUND_1 = 1;
		public static final int NOT_YET_REFUND_2 = 2;
		public static final int DONE_REFUND_3 = 3;
	}

	public static class Checkin_checkoutType {
		// 0-未退房 1-主动退房 2-自动退房 3-去呼呼同步退房 4-其他退房(例如换房对原来已经入住的单进行退房)
		public static final int NOT_CHECKOUT_1 = 0;
		public static final int MANUAL_CHECKOUT_1 = 1;
		public static final int TIMEOUT_CHECKOUT_2 = 2;
		public static final int QHH_SYNC_CHECKOUT_3 = 3;
		public static final int OTHER_4 = 4;
	}

	public static class BusiSmsRec_smsType {
		// 1-订单超时预警 2-订单超时 3-重发门锁密码短信 4-退房清洁短信 5-清洁申请短信
		public static final int ORDER_TIMEOUT_WARNING_SMS_1 = 1;
		public static final int ORDER_TIMEOUT_AUTO_CANCEL_2 = 2;
		public static final int RESEND_OPEN_DOOR_PWD_3 = 3;
		public static final int CLEAN_NOTIFICATION_CHECKOUT_4 = 4;
		public static final int CLEAN_NOTIFICATION_APPLY_5 = 5;
	}

	public static class Picture_picType {
		// 1-房间图片 2-导航图片
		public static final int ROOM_PIC_1 = 1;
		public static final int NAVI_MAP_PIC_2 = 2;
	}

	public static class Order_orderType {
		// 订单类型 1-客户订单 2-禁售单 5-测试订单 6-换房订单 7-业主订单 8-营销订单
		public static final int CUST_ORDER_1 = 1;
		public static final int SELF_FORBID_2 = 2;// 禁售单,其实就是房态控制单
		public static final int TEST_5 = 5;
		public static final int CHG_ROOM_6 = 6;
		public static final int ONWER_7 = 7;
		public static final int PROMO_8 = 8;
	}

	public static class Checkiner_sendFlag {
		// 1-要发送 2-不发送
		public static final int SEND_1 = 1;
		public static final int NOT_SEND_2 = 2;
	}
	public static class Checkiner_isMain{
		//1主要入住人　2　非主要入住人 default 2
		public static final int MAIN = 1;
		public static final int OTHER = 0;
	}
	public static class Checkiner_isDel{
		//1删除　0　正常 default 0
		public static final int DEL = 1;
		public static final int NORMAL = 0;
	}
	
	public static class CleanRec_handleSts {
		// 1未处理 3已接单 5已检查 7已完成 9已审核
		public static final int NOT_HANDLE_1 = 1;
		public static final int ACCEPT_TO_CLEAN_3 = 3;
		public static final int CHECKED_GOODS_5 = 5;
		public static final int FINISH_CLEAN_7 = 7;
		public static final int FINISH_AUDIT_9 = 9;
	}

	public static class CleanRec_srcType {
		// 1-退房变脏房 2-申请打扫变脏房
		public static final int CHECKOUT_DIRTY_1 = 1;
		public static final int APPLY_DIRTY_2 = 2;
	}

	// 用户来源
	public static class Lodger_isFollow {
		// 1-未关注 2-已关注
		public static final int NOT_FOLLOW_1 = 1;
		public static final int FOLLOW_2 = 2;
	}

	// 用户来源
	public static class Lodger_source {
		// 0:自己注册 1:预定注册 2:OTA渠道 3在checkin的时候被注册进来的 4房态控制单
		public static final int REGISTER_0 = 0;
		public static final int BOOK_1 = 1;// 预定的时候作为入住人被自动注册的
		public static final int OTA_2 = 2;
		public static final int CHECKIN_3 = 3;// 在checkin的时候被添加进来,被自动注册的
		public static final int OMS_ROOM_CTRL_4 = 4;// 房态控制单
		public static final int OWNER_5 = 5;// 业主APP过来,帮创建的帐号
		public static final int PROMO_6 = 6;// 活动注册的
	}

	// 支付状态
	public static class Order_payType {
		// 1-微信支付 2-未知（OTA来的订单，未知支付方式）
		public static final int WECHAT_1 = 1;
		public static final int UNKNOW_2 = 2;
	}

	// 去呼呼订单状态 orderStatusCode字段
	public static class Qunar_orderStat {
		// 状态 1-新订单 2-已分房 3-在店 4-离店 5-取消
		public static final int NEW_1 = 1;
		public static final int DISPATCH_2 = 2;
		public static final int IN_HOUSE_3 = 3;
		public static final int OUT_HOUSE_4 = 4;
		public static final int CANCEL_5 = 5;
	}
	
	public static class IFromQhh_syncStat {
		// 状态 0:新建 1:同步中 2:同步成功 3:同步失败 99:内容不正确
		public static final int NEW_0 = 0;
		public static final int HANDLING_1 = 1;
		public static final int DONE_SUCC_2 = 2;
		public static final int DONE_FAIL_3 = 3;
		public static final int WRONG_DATAGRAM_99 = 99;
	}

	// 去呼呼入住状态
	public static class Qunar_checkinStat {
		// //10 未入住 20在店 30离店 40取消
		public static final String NOT_CHECKIN_10 = "10";
		public static final String IN_HOUSE_20 = "20";
		public static final String OUT_HOUSE_30 = "30";
		public static final String CANCEL_40 = "40";
	}

	// 订单的状态
	public static class Order_stat {
		// 订单状态 0:未支付 1:已支付 2:取消(主动) 3:超时(被动) 4:取消未退款 5:取消且已退款
		public static final int NEW_0 = 0;
		public static final int PAYED_1 = 1;
		public static final int CANCEL_2 = 2;
		public static final int TIMEOUT_3 = 3;
		public static final int REFUND_APPLY_4 = 4;
		public static final int REFUND_5 = 5;
	}

	// 入住单的状态
	public static class Checkin_stat {
		// 状态 0:新建 1:已办理入住 2:已退房
		public static final int NEW_0 = 0;
		public static final int CHECKIN_1 = 1;
		public static final int CHECKOUT_2 = 2;
	}

	// 订单来源
	public static class Order_source {
		// 0-微信端 1-去哪儿 2-IOS端 3-安卓端 4-OMS 5-携程 6-业主APP 7-美团
		public static final int WECHAT_0 = 0;
		public static final int QUNAR_1 = 1;
		public static final int IOS_2 = 2;
		public static final int ANDROID_3 = 3;
		public static final int OMS_4 = 4;
		public static final int CTRIP_5 = 5;
		public static final int OWNER_6 = 6;
		public static final int MEITUAN_7 = 7;
	}

	public static class Sync_syncResult {
		// 1-未同步(默认) 2-同步成功 3-同步失败
		public static final byte NOT_SYNC_1 = 1;
		public static final byte SYNC_SUCC_2 = 2;
		public static final byte SYNC_FAIL_3 = 3;
	}

	public static class Room_flag {
		// 1-有效 2-删除
		public static final int NORMAL_1 = 1;
		public static final int DELETED_2 = 2;
	}
	
	public static class RoomState_stat {
		// 状态1 干净房 ，2 脏房 ， 3 已入住， 4 停用，5 维修
		public static final int CLEAN_1 = 1;
		public static final int DIRTY_2 = 2;
		public static final int CHECKIN_3 = 3;
		public static final int STOP_4 = 4;
		public static final int FIX_5 = 5;
	}

	public static class Facility_flag {
		// 1-有效 2-删除
		public static final int NORMAL_1 = 1;
		public static final int DELETED_2 = 2;
	}

	public static class Facility_facilityType {
		// 1-基础设施 2-提供的服务
		public static final int BASE_FACILITY_1 = 1;
		public static final int PROV_SERVICE_2 = 2;
	}

	public static class Calendar_stat {
		// 1可预订 2 预售 3 维修 4 停用
		public static final int FREE_1 = 1;
		public static final int SALE_2 = 2;
		public static final int FIX_3 = 3;
		public static final int STOP_4 = 4;
	}

	public static class CalendarDaily_stat {
		// 1解锁(可订) 2锁定(预占) 3锁定(禁售)
		public static final int FREE_1 = 1;
		public static final int LOCKED_PREEMPT_2 = 2;
		public static final int LOCKED_FORBIDDEN_3 = 3;
	}

	public static class Schedule_stat {
		// 1:可售 2:禁售(仅自有渠道)
		public static final int SALE_PERMITTED_1 = 1;
		public static final int SALE_FORBIDDEN_2 = 2;
	}

	public static class Picture_cover {
		// 0:否 1:是
		public static final int NOT_COVER_0 = 0;
		public static final int IS_COVER_1 = 1;
	}

	public static class Lodger_stat {
		// 1-正常 2-注销
		public static final int NORMAL_1 = 1;
		public static final int DELETED_2 = 2;
	}

}
