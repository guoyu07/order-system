package cn.com.xbed.app.bean;

import java.util.Date;

public class XbQhhSyncLog implements java.io.Serializable {

	private Integer logId;
	private String otaOrderNo;
	private String orderStatusCode;
	private Integer succ;
	private String datagram;
	private Date createDtm;
	private String orderMan;
	private String orderMobile;
	private String failMsg;
	private String remark;

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public String getOtaOrderNo() {
		return otaOrderNo;
	}

	public void setOtaOrderNo(String otaOrderNo) {
		this.otaOrderNo = otaOrderNo;
	}

	public String getOrderStatusCode() {
		return orderStatusCode;
	}

	public void setOrderStatusCode(String orderStatusCode) {
		this.orderStatusCode = orderStatusCode;
	}

	public Integer getSucc() {
		return succ;
	}

	public void setSucc(Integer succ) {
		this.succ = succ;
	}

	public String getDatagram() {
		return datagram;
	}

	public void setDatagram(String datagram) {
		this.datagram = datagram;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getOrderMan() {
		return orderMan;
	}

	public void setOrderMan(String orderMan) {
		this.orderMan = orderMan;
	}

	public String getOrderMobile() {
		return orderMobile;
	}

	public void setOrderMobile(String orderMobile) {
		this.orderMobile = orderMobile;
	}

	public String getFailMsg() {
		return failMsg;
	}

	public void setFailMsg(String failMsg) {
		this.failMsg = failMsg;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
