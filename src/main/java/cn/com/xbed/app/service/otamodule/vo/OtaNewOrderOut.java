package cn.com.xbed.app.service.otamodule.vo;

public class OtaNewOrderOut {
	private String otaOrderNo;
	private String otaStayId;
	private int roomId;
	private int checkinId;

	public String getOtaOrderNo() {
		return otaOrderNo;
	}

	public String getOtaStayId() {
		return otaStayId;
	}

	public int getRoomId() {
		return roomId;
	}

	public int getCheckinId() {
		return checkinId;
	}

	public OtaNewOrderOut(String otaOrderNo, String otaStayId, int roomId, int checkinId) {
		super();
		this.otaOrderNo = otaOrderNo;
		this.otaStayId = otaStayId;
		this.roomId = roomId;
		this.checkinId = checkinId;
	}

}
