package cn.com.xbed.app.service.syncin.vo;

public class SyncRoomEntity {
	private String checkInStatus;
	private String checkInStatusMsg;
	private String chkinTime;
	private String chkoutTime;
	private String customerName;
	private String customerTelNo;
	private String stayID;
	private String realChkinTime;
	private String realChkoutTime;
	private String roomId;
	private String roomPrice;

	public String getCheckInStatus() {
		return checkInStatus;
	}

	public void setCheckInStatus(String checkInStatus) {
		this.checkInStatus = checkInStatus;
	}

	public String getCheckInStatusMsg() {
		return checkInStatusMsg;
	}

	public void setCheckInStatusMsg(String checkInStatusMsg) {
		this.checkInStatusMsg = checkInStatusMsg;
	}

	public String getChkinTime() {
		return chkinTime;
	}

	public void setChkinTime(String chkinTime) {
		this.chkinTime = chkinTime;
	}

	public String getChkoutTime() {
		return chkoutTime;
	}

	public void setChkoutTime(String chkoutTime) {
		this.chkoutTime = chkoutTime;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerTelNo() {
		return customerTelNo;
	}

	public void setCustomerTelNo(String customerTelNo) {
		this.customerTelNo = customerTelNo;
	}

	public String getStayID() {
		return stayID;
	}

	public void setStayID(String stayID) {
		this.stayID = stayID;
	}

	public String getRealChkinTime() {
		return realChkinTime;
	}

	public void setRealChkinTime(String realChkinTime) {
		this.realChkinTime = realChkinTime;
	}

	public String getRealChkoutTime() {
		return realChkoutTime;
	}

	public void setRealChkoutTime(String realChkoutTime) {
		this.realChkoutTime = realChkoutTime;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomPrice() {
		return roomPrice;
	}

	public void setRoomPrice(String roomPrice) {
		this.roomPrice = roomPrice;
	}
}