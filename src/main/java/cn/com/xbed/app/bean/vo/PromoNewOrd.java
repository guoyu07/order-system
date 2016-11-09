package cn.com.xbed.app.bean.vo;

public class PromoNewOrd {
	private String orderMan;
	private String orderManMobile;

	private String checkinManName;
	private String checkinManMobile;

	private String checkinTime;// 传2015-12-29格式
	private String checkoutTime;// 传2015-12-30格式
	private int roomId;

	private String memo;

	private int userId;
	private String userAcct;

	public String getOrderMan() {
		return orderMan;
	}

	public String getOrderManMobile() {
		return orderManMobile;
	}

	public String getCheckinManName() {
		return checkinManName;
	}

	public String getCheckinManMobile() {
		return checkinManMobile;
	}

	public String getCheckinTime() {
		return checkinTime;
	}

	public String getCheckoutTime() {
		return checkoutTime;
	}

	public int getRoomId() {
		return roomId;
	}

	public String getMemo() {
		return memo;
	}

	public int getUserId() {
		return userId;
	}

	public String getUserAcct() {
		return userAcct;
	}

	public void setOrderMan(String orderMan) {
		this.orderMan = orderMan;
	}

	public void setOrderManMobile(String orderManMobile) {
		this.orderManMobile = orderManMobile;
	}

	public void setCheckinManName(String checkinManName) {
		this.checkinManName = checkinManName;
	}

	public void setCheckinManMobile(String checkinManMobile) {
		this.checkinManMobile = checkinManMobile;
	}

	public void setCheckinTime(String checkinTime) {
		this.checkinTime = checkinTime;
	}

	public void setCheckoutTime(String checkoutTime) {
		this.checkoutTime = checkoutTime;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUserAcct(String userAcct) {
		this.userAcct = userAcct;
	}

	public PromoNewOrd(String orderMan, String orderManMobile, String checkinManName, String checkinManMobile, String checkinTime,
			String checkoutTime, int roomId, String memo, int userId, String userAcct) {
		super();
		this.orderMan = orderMan;
		this.orderManMobile = orderManMobile;
		this.checkinManName = checkinManName;
		this.checkinManMobile = checkinManMobile;
		this.checkinTime = checkinTime;
		this.checkoutTime = checkoutTime;
		this.roomId = roomId;
		this.memo = memo;
		this.userId = userId;
		this.userAcct = userAcct;
	}

	public PromoNewOrd() {
		super();
	}

	@Override
	public String toString() {
		return "PromoNewOrd [orderMan=" + orderMan + ", orderManMobile=" + orderManMobile + ", checkinManName=" + checkinManName
				+ ", checkinManMobile=" + checkinManMobile + ", checkinTime=" + checkinTime + ", checkoutTime=" + checkoutTime + ", roomId=" + roomId
				+ ", memo=" + memo + ", userId=" + userId + ", userAcct=" + userAcct + "]";
	}

}
