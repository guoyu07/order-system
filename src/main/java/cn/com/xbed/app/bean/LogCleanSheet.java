package cn.com.xbed.app.bean;

import java.util.Date;

public class LogCleanSheet implements java.io.Serializable {

	private Integer sheetId;
	private Date createDtm;
	private String orderNo;
	private Integer checkinId;
	private Integer plan;
	private Integer roomId;
	private Integer cleanSource;
	private String datagram;
	private String remark;
	private String succFlag;

	
	
	@Override
	public String toString() {
		return "LogCleanSheet [sheetId=" + sheetId + ", createDtm=" + createDtm + ", orderNo=" + orderNo + ", checkinId=" + checkinId + ", plan="
				+ plan + ", roomId=" + roomId + ", cleanSource=" + cleanSource + ", datagram=" + datagram + ", remark=" + remark + ", succFlag="
				+ succFlag + "]";
	}

	public LogCleanSheet() {
	}

	public LogCleanSheet(Date createDtm, String orderNo, Integer checkinId, Integer plan, Integer roomId, Integer cleanSource, String datagram,
			String remark, String succFlag) {
		this.createDtm = createDtm;
		this.orderNo = orderNo;
		this.checkinId = checkinId;
		this.plan = plan;
		this.roomId = roomId;
		this.cleanSource = cleanSource;
		this.datagram = datagram;
		this.remark = remark;
		this.succFlag = succFlag;
	}

	public Integer getSheetId() {
		return this.sheetId;
	}

	public void setSheetId(Integer sheetId) {
		this.sheetId = sheetId;
	}

	public Date getCreateDtm() {
		return this.createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(Integer checkinId) {
		this.checkinId = checkinId;
	}

	public Integer getPlan() {
		return plan;
	}

	public void setPlan(Integer plan) {
		this.plan = plan;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getCleanSource() {
		return cleanSource;
	}

	public void setCleanSource(Integer cleanSource) {
		this.cleanSource = cleanSource;
	}

	public String getDatagram() {
		return datagram;
	}

	public void setDatagram(String datagram) {
		this.datagram = datagram;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSuccFlag() {
		return succFlag;
	}

	public void setSuccFlag(String succFlag) {
		this.succFlag = succFlag;
	}

}
