package cn.com.xbed.app.bean;

import java.util.Date;

public class QhhOrder implements java.io.Serializable {

	private Integer orderSn;
	private Integer preOrderSn;
	private String XOrderNo;
	private String OOrderNo;
	private Date chkinDtm;
	private Date chkoutDtm;
	private String contactName;
	private String contactMobile;
	private Date XUpdate;
	private Date OUpdate;
	private String stat;
	private String payType;
	private Integer rentMoney;

	public Integer getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(Integer orderSn) {
		this.orderSn = orderSn;
	}

	public Integer getPreOrderSn() {
		return preOrderSn;
	}

	public void setPreOrderSn(Integer preOrderSn) {
		this.preOrderSn = preOrderSn;
	}

	public String getXOrderNo() {
		return XOrderNo;
	}

	public void setXOrderNo(String xOrderNo) {
		XOrderNo = xOrderNo;
	}

	public String getOOrderNo() {
		return OOrderNo;
	}

	public void setOOrderNo(String oOrderNo) {
		OOrderNo = oOrderNo;
	}

	public Date getChkinDtm() {
		return chkinDtm;
	}

	public void setChkinDtm(Date chkinDtm) {
		this.chkinDtm = chkinDtm;
	}

	public Date getChkoutDtm() {
		return chkoutDtm;
	}

	public void setChkoutDtm(Date chkoutDtm) {
		this.chkoutDtm = chkoutDtm;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public Date getXUpdate() {
		return XUpdate;
	}

	public void setXUpdate(Date xUpdate) {
		XUpdate = xUpdate;
	}

	public Date getOUpdate() {
		return OUpdate;
	}

	public void setOUpdate(Date oUpdate) {
		OUpdate = oUpdate;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Integer getRentMoney() {
		return rentMoney;
	}

	public void setRentMoney(Integer rentMoney) {
		this.rentMoney = rentMoney;
	}

}
