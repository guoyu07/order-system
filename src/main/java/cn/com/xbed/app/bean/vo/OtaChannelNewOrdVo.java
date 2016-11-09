package cn.com.xbed.app.bean.vo;

import java.util.List;

public class OtaChannelNewOrdVo {
	private String otaChannelOrderNo;
	//
	private String orderMan;
	private String orderManMobile;
	private List<Checkiner> checkinerList;
	private String checkinTime;// 传2015-12-29格式
	private String checkoutTime;// 传2015-12-30格式

	private int channel;// 1-携程  2-美团
	private int userId;
	private String userAcct;

	private String memo;

	private int totalPrice;// 单位分

	
	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public String getOtaChannelOrderNo() {
		return otaChannelOrderNo;
	}

	public void setOtaChannelOrderNo(String otaChannelOrderNo) {
		this.otaChannelOrderNo = otaChannelOrderNo;
	}

	public String getOrderMan() {
		return orderMan;
	}

	public void setOrderMan(String orderMan) {
		this.orderMan = orderMan;
	}

	public String getOrderManMobile() {
		return orderManMobile;
	}

	public void setOrderManMobile(String orderManMobile) {
		this.orderManMobile = orderManMobile;
	}

	public List<Checkiner> getCheckinerList() {
		return checkinerList;
	}

	public void setCheckinerList(List<Checkiner> checkinerList) {
		this.checkinerList = checkinerList;
	}

	public String getCheckinTime() {
		return checkinTime;
	}

	public void setCheckinTime(String checkinTime) {
		this.checkinTime = checkinTime;
	}

	public String getCheckoutTime() {
		return checkoutTime;
	}

	public void setCheckoutTime(String checkoutTime) {
		this.checkoutTime = checkoutTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserAcct() {
		return userAcct;
	}

	public void setUserAcct(String userAcct) {
		this.userAcct = userAcct;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public OtaChannelNewOrdVo(String otaChannelOrderNo, String orderMan, String orderManMobile, List<Checkiner> checkinerList, String checkinTime,
			String checkoutTime, int userId, String userAcct, String memo, int totalPrice, int channel) {
		super();
		this.otaChannelOrderNo = otaChannelOrderNo;
		this.orderMan = orderMan;
		this.orderManMobile = orderManMobile;
		this.checkinerList = checkinerList;
		this.checkinTime = checkinTime;
		this.checkoutTime = checkoutTime;
		this.userId = userId;
		this.userAcct = userAcct;
		this.memo = memo;
		this.totalPrice = totalPrice;
		this.channel = channel;
	}

	public OtaChannelNewOrdVo() {
		super();
	}

	public static class Checkiner {
		private String checkinManName;
		private String checkinManMobile;
		private int roomId;
		private int checkinPrice;

		public Checkiner(String checkinManName, String checkinManMobile, int roomId, int checkinPrice) {
			super();
			this.checkinManName = checkinManName;
			this.checkinManMobile = checkinManMobile;
			this.roomId = roomId;
			this.checkinPrice = checkinPrice;
		}

		public Checkiner() {
			super();
		}

		public String getCheckinManName() {
			return checkinManName;
		}

		public void setCheckinManName(String checkinManName) {
			this.checkinManName = checkinManName;
		}

		public String getCheckinManMobile() {
			return checkinManMobile;
		}

		public void setCheckinManMobile(String checkinManMobile) {
			this.checkinManMobile = checkinManMobile;
		}

		public int getRoomId() {
			return roomId;
		}

		public void setRoomId(int roomId) {
			this.roomId = roomId;
		}

		public int getCheckinPrice() {
			return checkinPrice;
		}

		public void setCheckinPrice(int checkinPrice) {
			this.checkinPrice = checkinPrice;
		}

		@Override
		public String toString() {
			return "Checkiner [checkinManName=" + checkinManName + ", checkinManMobile=" + checkinManMobile + ", roomId=" + roomId + ", checkinPrice="
					+ checkinPrice + "]";
		}

	}

	@Override
	public String toString() {
		return "OtaChannelNewOrdVo [otaChannelOrderNo=" + otaChannelOrderNo + ", orderMan=" + orderMan + ", orderManMobile=" + orderManMobile
				+ ", checkinerList=" + checkinerList + ", checkinTime=" + checkinTime + ", checkoutTime=" + checkoutTime + ", channel=" + channel
				+ ", userId=" + userId + ", userAcct=" + userAcct + ", memo=" + memo + ", totalPrice=" + totalPrice + "]";
	}

	
}
