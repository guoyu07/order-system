package cn.com.xbed.app.service.stopmodule.vo;

public class StopEntity {
	private Integer roomId;
	private String stopBegin;
	private String stopEnd;
	private String contactName;
	private String contactMobile;
	private Integer userId;
	private String userAcct;
	private String memo;

	public StopEntity(Integer roomId, String stopBegin, String stopEnd, String contactName, String contactMobile, Integer userId, String userAcct,
			String memo) {
		super();
		this.roomId = roomId;
		this.stopBegin = stopBegin;
		this.stopEnd = stopEnd;
		this.contactName = contactName;
		this.contactMobile = contactMobile;
		this.userId = userId;
		this.userAcct = userAcct;
		this.memo = memo;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getUserAcct() {
		return userAcct;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public String getStopBegin() {
		return stopBegin;
	}

	public String getStopEnd() {
		return stopEnd;
	}

	public String getContactName() {
		return contactName;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public String getMemo() {
		return memo;
	}

}
