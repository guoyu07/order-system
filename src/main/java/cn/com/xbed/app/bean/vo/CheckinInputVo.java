package cn.com.xbed.app.bean.vo;

public class CheckinInputVo {
	private int checkinId;
	private CheckinerVo checkiner;

	public CheckinInputVo() {
		super();
	}

	public CheckinInputVo(int checkinId, CheckinerVo checkiner) {
		super();
		this.checkinId = checkinId;
		this.checkiner = checkiner;
	}

	public int getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(int checkinId) {
		this.checkinId = checkinId;
	}

	public CheckinerVo getCheckiner() {
		return checkiner;
	}

	public void setCheckiner(CheckinerVo checkiner) {
		this.checkiner = checkiner;
	}

	@Override
	public String toString() {
		return "CheckinInputVo [checkinId=" + checkinId + ", checkiner=" + checkiner + "]";
	}

}
