package cn.com.xbed.app.service.localordermodule.vo;

import java.util.Date;

public class CheckinInput {
	private int roomId;
	private int checkinLodgerId;
	private String checkinName;
	private String checkinMobile;
	private Date checkinBeginTime;
	private Date checkinEndTime;
	private int chkinPriceCentUnit;
	private String otaStayId;
	private boolean saveXbCheckiner = true;// true保存,false不保存。xb_checkiner

	public CheckinInput(int roomId, int checkinLodgerId, String checkinName, String checkinMobile, Date checkinBeginTime, Date checkinEndTime,
			int chkinPriceCentUnit, String otaStayId) {
		super();
		this.roomId = roomId;
		this.checkinLodgerId = checkinLodgerId;
		this.checkinName = checkinName;
		this.checkinMobile = checkinMobile;
		this.checkinBeginTime = checkinBeginTime;
		this.checkinEndTime = checkinEndTime;
		this.chkinPriceCentUnit = chkinPriceCentUnit;
		this.otaStayId = otaStayId;
	}

	public String getOtaStayId() {
		return otaStayId;
	}

	public int getRoomId() {
		return roomId;
	}

	public int getCheckinLodgerId() {
		return checkinLodgerId;
	}

	public String getCheckinName() {
		return checkinName;
	}

	public String getCheckinMobile() {
		return checkinMobile;
	}

	public Date getCheckinBeginTime() {
		return checkinBeginTime;
	}

	public Date getCheckinEndTime() {
		return checkinEndTime;
	}

	public int getChkinPriceCentUnit() {
		return chkinPriceCentUnit;
	}

	
	public boolean isSaveXbCheckiner() {
		return saveXbCheckiner;
	}

	public void setSaveXbCheckiner(boolean saveXbCheckiner) {
		this.saveXbCheckiner = saveXbCheckiner;
	}

	@Override
	public String toString() {
		return "CheckinInput [roomId=" + roomId + ", checkinLodgerId=" + checkinLodgerId + ", checkinName=" + checkinName + ", checkinMobile="
				+ checkinMobile + ", checkinBeginTime=" + checkinBeginTime + ", checkinEndTime=" + checkinEndTime + ", chkinPriceCentUnit="
				+ chkinPriceCentUnit + ", otaStayId=" + otaStayId + ", saveXbCheckiner=" + saveXbCheckiner + "]";
	}
}