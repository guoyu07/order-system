package cn.com.xbed.app.bean;

import java.util.Date;

public class QhhRoom implements java.io.Serializable {

	private Integer roomSn;
	private Integer preRoomSn;
	private Integer orderSn;
	private Integer roomId;
	private String stat;
	private Date chkinDtm;
	private Date chkoutDtm;
	private String customerName;
	private String customerMobile;
	private String stayId;
	private String realChkinDtm;
	private String realChkoutDtm;
	private Integer price;
	private Date modiTime;

	public Integer getRoomSn() {
		return roomSn;
	}

	public void setRoomSn(Integer roomSn) {
		this.roomSn = roomSn;
	}

	public Integer getPreRoomSn() {
		return preRoomSn;
	}

	public void setPreRoomSn(Integer preRoomSn) {
		this.preRoomSn = preRoomSn;
	}

	public Integer getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(Integer orderSn) {
		this.orderSn = orderSn;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getStayId() {
		return stayId;
	}

	public void setStayId(String stayId) {
		this.stayId = stayId;
	}

	public String getRealChkinDtm() {
		return realChkinDtm;
	}

	public void setRealChkinDtm(String realChkinDtm) {
		this.realChkinDtm = realChkinDtm;
	}

	public String getRealChkoutDtm() {
		return realChkoutDtm;
	}

	public void setRealChkoutDtm(String realChkoutDtm) {
		this.realChkoutDtm = realChkoutDtm;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Date getModiTime() {
		return modiTime;
	}

	public void setModiTime(Date modiTime) {
		this.modiTime = modiTime;
	}

}
