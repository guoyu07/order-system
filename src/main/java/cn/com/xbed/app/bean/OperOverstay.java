package cn.com.xbed.app.bean;

import java.util.Date;

public class OperOverstay implements java.io.Serializable {
	private Integer operId;
	private Integer oriCheckinId;
	private Integer newCheckinId;
	private Integer oriOrderId;
	private Integer newOrderId;
	private Integer oriRoomId;
	private Integer newRoomId;
	private Integer lodgerId;
	private Date createDtm;
	private String remark;

	public OperOverstay() {
		super();
	}

	public OperOverstay(Integer oriCheckinId, Integer newCheckinId, Integer oriOrderId, Integer newOrderId, Integer oriRoomId, Integer newRoomId,
			Integer lodgerId, Date createDtm) {
		super();
		this.oriCheckinId = oriCheckinId;
		this.newCheckinId = newCheckinId;
		this.oriOrderId = oriOrderId;
		this.newOrderId = newOrderId;
		this.oriRoomId = oriRoomId;
		this.newRoomId = newRoomId;
		this.lodgerId = lodgerId;
		this.createDtm = createDtm;
	}

	public OperOverstay(Integer oriCheckinId, Integer newCheckinId, Integer oriOrderId, Integer newOrderId, Integer oriRoomId, Integer newRoomId,
			Integer lodgerId, Date createDtm, String remark) {
		super();
		this.oriCheckinId = oriCheckinId;
		this.newCheckinId = newCheckinId;
		this.oriOrderId = oriOrderId;
		this.newOrderId = newOrderId;
		this.oriRoomId = oriRoomId;
		this.newRoomId = newRoomId;
		this.lodgerId = lodgerId;
		this.createDtm = createDtm;
		this.remark = remark;
	}

	public Integer getOperId() {
		return operId;
	}

	public void setOperId(Integer operId) {
		this.operId = operId;
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

	public Integer getOriRoomId() {
		return oriRoomId;
	}

	public void setOriRoomId(Integer oriRoomId) {
		this.oriRoomId = oriRoomId;
	}

	public Integer getNewRoomId() {
		return newRoomId;
	}

	public void setNewRoomId(Integer newRoomId) {
		this.newRoomId = newRoomId;
	}

	public Integer getLodgerId() {
		return lodgerId;
	}

	public void setLodgerId(Integer lodgerId) {
		this.lodgerId = lodgerId;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "OperOverstay [operId=" + operId + ", oriCheckinId=" + oriCheckinId + ", newCheckinId=" + newCheckinId + ", oriOrderId=" + oriOrderId
				+ ", newOrderId=" + newOrderId + ", oriRoomId=" + oriRoomId + ", newRoomId=" + newRoomId + ", lodgerId=" + lodgerId + ", createDtm="
				+ createDtm + ", remark=" + remark + "]";
	}

}
