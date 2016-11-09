package cn.com.xbed.app.bean;

import java.util.Date;

public class XbOrderCtrip implements java.io.Serializable {

	private Integer ctripId;
	private String ctripNo;
	private Integer orderId;
	private String orderNo;
	private String memo;
	private Date createDtm;
	private String datagram;
	private Integer userId;
	private String userAcct;

	public XbOrderCtrip() {
		super();
	}

	public XbOrderCtrip(String ctripNo, Integer orderId, String orderNo, String memo, Date createDtm, String datagram,
			Integer userId, String userAcct) {
		super();
		this.ctripNo = ctripNo;
		this.orderId = orderId;
		this.orderNo = orderNo;
		this.memo = memo;
		this.createDtm = createDtm;
		this.datagram = datagram;
		this.userId = userId;
		this.userAcct = userAcct;
	}

	public Integer getCtripId() {
		return ctripId;
	}

	public void setCtripId(Integer ctripId) {
		this.ctripId = ctripId;
	}

	public String getCtripNo() {
		return ctripNo;
	}

	public void setCtripNo(String ctripNo) {
		this.ctripNo = ctripNo;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserAcct() {
		return userAcct;
	}

	public void setUserAcct(String userAcct) {
		this.userAcct = userAcct;
	}

}
