package cn.com.xbed.app.service.syncin.vo;

import java.util.List;

public class SyncOrdEntity {
	private Integer jobId;// 用于改处理标记
	private String orderNo;
	private String channelID;
	private String channelName;
	private String channelOrderNo;
	private String checkInTime;
	private String checkOutTime;
	private String contactName;
	private String contactMobile;
	private String upDate;
	private String orderStatusCode;
	private String orderStatusMsg;
	private String payTypeCode;
	private String payTypeMsg;
	private String rentMoney;
	private List<SyncRoomEntity> roomInfos;

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getChannelID() {
		return channelID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelOrderNo() {
		return channelOrderNo;
	}

	public void setChannelOrderNo(String channelOrderNo) {
		this.channelOrderNo = channelOrderNo;
	}

	public String getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}

	public String getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
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

	public String getUpDate() {
		return upDate;
	}

	public void setUpDate(String upDate) {
		this.upDate = upDate;
	}

	public String getOrderStatusCode() {
		return orderStatusCode;
	}

	public void setOrderStatusCode(String orderStatusCode) {
		this.orderStatusCode = orderStatusCode;
	}

	public String getOrderStatusMsg() {
		return orderStatusMsg;
	}

	public void setOrderStatusMsg(String orderStatusMsg) {
		this.orderStatusMsg = orderStatusMsg;
	}

	public String getPayTypeCode() {
		return payTypeCode;
	}

	public void setPayTypeCode(String payTypeCode) {
		this.payTypeCode = payTypeCode;
	}

	public String getPayTypeMsg() {
		return payTypeMsg;
	}

	public void setPayTypeMsg(String payTypeMsg) {
		this.payTypeMsg = payTypeMsg;
	}

	public String getRentMoney() {
		return rentMoney;
	}

	public void setRentMoney(String rentMoney) {
		this.rentMoney = rentMoney;
	}

	public List<SyncRoomEntity> getRoomInfos() {
		return roomInfos;
	}

	public void setRoomInfos(List<SyncRoomEntity> roomInfos) {
		this.roomInfos = roomInfos;
	}

	@Override
	public String toString() {
		return "SyncOrdEntity [orderNo=" + orderNo + ", orderStatusCode=" + orderStatusCode + "]";
	}

}