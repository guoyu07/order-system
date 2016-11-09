package cn.com.xbed.app.service.maintainmodule.vo;

public class OpenPwdEntity {
	private int checkinId;
	private String openPwd;

	public int getCheckinId() {
		return checkinId;
	}

	public String getOpenPwd() {
		return openPwd;
	}

	public OpenPwdEntity(int checkinId, String openPwd) {
		super();
		this.checkinId = checkinId;
		this.openPwd = openPwd;
	}

}