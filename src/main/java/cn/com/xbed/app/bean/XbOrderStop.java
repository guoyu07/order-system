package cn.com.xbed.app.bean;

import java.util.Date;

public class XbOrderStop implements java.io.Serializable {
	private Integer stopId;
	private String stopNo;
	private Integer stopStat;
	private Integer roomId;
	private String roomNo;
	private Date stopBegin;
	private Date stopEnd;
	private Date actualStopBegin;
	private Date actualStopEnd;
	private String contactMobile;
	private String contactName;
	private String chainName;
	private Integer chainId;
	private Date createDtm;
	private String memo;
	private String remark;
	private Integer stopDays;
	private Integer price;
	private Date lastModDtm;
	private Integer userId;
	private String userAcct;
	private String otaOrderNo;
	private String otaStayId;
	private String openPwd;

	public Date getActualStopBegin() {
		return actualStopBegin;
	}

	public void setActualStopBegin(Date actualStopBegin) {
		this.actualStopBegin = actualStopBegin;
	}

	public Date getActualStopEnd() {
		return actualStopEnd;
	}

	public void setActualStopEnd(Date actualStopEnd) {
		this.actualStopEnd = actualStopEnd;
	}

	public Integer getStopId() {
		return stopId;
	}

	public void setStopId(Integer stopId) {
		this.stopId = stopId;
	}

	public String getStopNo() {
		return stopNo;
	}

	public void setStopNo(String stopNo) {
		this.stopNo = stopNo;
	}

	public Integer getStopStat() {
		return stopStat;
	}

	public void setStopStat(Integer stopStat) {
		this.stopStat = stopStat;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public Date getStopBegin() {
		return stopBegin;
	}

	public void setStopBegin(Date stopBegin) {
		this.stopBegin = stopBegin;
	}

	public Date getStopEnd() {
		return stopEnd;
	}

	public void setStopEnd(Date stopEnd) {
		this.stopEnd = stopEnd;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getChainName() {
		return chainName;
	}

	public void setChainName(String chainName) {
		this.chainName = chainName;
	}

	public Integer getChainId() {
		return chainId;
	}

	public void setChainId(Integer chainId) {
		this.chainId = chainId;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getStopDays() {
		return stopDays;
	}

	public void setStopDays(Integer stopDays) {
		this.stopDays = stopDays;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Date getLastModDtm() {
		return lastModDtm;
	}

	public void setLastModDtm(Date lastModDtm) {
		this.lastModDtm = lastModDtm;
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

	public String getOtaOrderNo() {
		return otaOrderNo;
	}

	public void setOtaOrderNo(String otaOrderNo) {
		this.otaOrderNo = otaOrderNo;
	}

	public String getOtaStayId() {
		return otaStayId;
	}

	public void setOtaStayId(String otaStayId) {
		this.otaStayId = otaStayId;
	}

	public String getOpenPwd() {
		return openPwd;
	}

	public void setOpenPwd(String openPwd) {
		this.openPwd = openPwd;
	}

}
