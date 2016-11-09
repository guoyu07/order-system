package cn.com.xbed.app.service.maintainmodule.vo;

public class OtaStayIdEntity {
	private int checkinId;
	private String otaStayId;

	public int getCheckinId() {
		return checkinId;
	}

	public String getOtaStayId() {
		return otaStayId;
	}

	public OtaStayIdEntity(int checkinId, String otaStayId) {
		super();
		this.checkinId = checkinId;
		this.otaStayId = otaStayId;
	}

}