package cn.com.xbed.app.bean;

import java.util.Date;

public class XbOperLogChgroom implements java.io.Serializable {
	private Integer operId;
	private Integer oriOrderId;
	private Integer newOrderId;
	private Integer oriCheckinId;
	private Integer newCheckinId;
	private Date chgroomDtm;
	private Integer userId;
	private Date lastModDtm;
	private String memo;

	public Integer getOperId() {
		return operId;
	}

	public void setOperId(Integer operId) {
		this.operId = operId;
	}

	public Integer getOriOrderId() {
		return oriOrderId;
	}

	public void setOriOrderId(Integer oriOrderId) {
		this.oriOrderId = oriOrderId;
	}

	public Integer getNewOrderId() {
		return newOrderId;
	}

	public void setNewOrderId(Integer newOrderId) {
		this.newOrderId = newOrderId;
	}

	public Integer getOriCheckinId() {
		return oriCheckinId;
	}

	public void setOriCheckinId(Integer oriCheckinId) {
		this.oriCheckinId = oriCheckinId;
	}

	public Integer getNewCheckinId() {
		return newCheckinId;
	}

	public void setNewCheckinId(Integer newCheckinId) {
		this.newCheckinId = newCheckinId;
	}

	public Date getChgroomDtm() {
		return chgroomDtm;
	}

	public void setChgroomDtm(Date chgroomDtm) {
		this.chgroomDtm = chgroomDtm;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getLastModDtm() {
		return lastModDtm;
	}

	public void setLastModDtm(Date lastModDtm) {
		this.lastModDtm = lastModDtm;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
	public String toString() {
		return "XbOperLogChgroom [operId=" + operId + ", oriOrderId=" + oriOrderId + ", newOrderId=" + newOrderId + ", oriCheckinId=" + oriCheckinId
				+ ", newCheckinId=" + newCheckinId + ", chgroomDtm=" + chgroomDtm + ", userId=" + userId + ", lastModDtm=" + lastModDtm + ", memo="
				+ memo + "]";
	}

}
