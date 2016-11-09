package cn.com.xbed.app.bean;

import java.util.Date;

public class XbQunarOrderOper implements java.io.Serializable {

	private Integer operId;
	private String otaOrderNo;
	private String otaOrderStatusCode;
	private String otaStayId;
	private Integer localOrderId;
	private String localOrderNo;
	private String localRoomId;
	private String remark;
	private Date createDtm;
	private Date checkinTime;
	private Date checkoutTime;
	private String datagram;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCheckinTime() {
		return checkinTime;
	}

	public void setCheckinTime(Date checkinTime) {
		this.checkinTime = checkinTime;
	}

	public Date getCheckoutTime() {
		return checkoutTime;
	}

	public void setCheckoutTime(Date checkoutTime) {
		this.checkoutTime = checkoutTime;
	}

	public String getOtaStayId() {
		return otaStayId;
	}

	public void setOtaStayId(String otaStayId) {
		this.otaStayId = otaStayId;
	}

	public Integer getLocalOrderId() {
		return localOrderId;
	}

	public void setLocalOrderId(Integer localOrderId) {
		this.localOrderId = localOrderId;
	}

	public String getLocalOrderNo() {
		return localOrderNo;
	}

	public void setLocalOrderNo(String localOrderNo) {
		this.localOrderNo = localOrderNo;
	}

	public String getLocalRoomId() {
		return localRoomId;
	}

	public void setLocalRoomId(String localRoomId) {
		this.localRoomId = localRoomId;
	}

	public Integer getOperId() {
		return operId;
	}

	public void setOperId(Integer operId) {
		this.operId = operId;
	}

	public String getOtaOrderNo() {
		return otaOrderNo;
	}

	public void setOtaOrderNo(String otaOrderNo) {
		this.otaOrderNo = otaOrderNo;
	}

	public String getOtaOrderStatusCode() {
		return otaOrderStatusCode;
	}

	public void setOtaOrderStatusCode(String otaOrderStatusCode) {
		this.otaOrderStatusCode = otaOrderStatusCode;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getDatagram() {
		return datagram;
	}

	public void setDatagram(String datagram) {
		this.datagram = datagram;
	}

	@Override
	public String toString() {
		return "XbQunarOrderOper [operId=" + operId + ", otaOrderNo=" + otaOrderNo + ", otaOrderStatusCode=" + otaOrderStatusCode + ", otaStayId="
				+ otaStayId + ", localOrderId=" + localOrderId + ", localOrderNo=" + localOrderNo + ", localRoomId=" + localRoomId + ", remark="
				+ remark + ", createDtm=" + createDtm + ", checkinTime=" + checkinTime + ", checkoutTime=" + checkoutTime + ", datagram=" + datagram
				+ "]";
	}

}
