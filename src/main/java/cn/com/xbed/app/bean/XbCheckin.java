package cn.com.xbed.app.bean;

import java.util.Date;

public class XbCheckin implements java.io.Serializable {

	private Integer checkinId;
	private Integer orderId;
	private Integer lodgerId;
	private Integer roomId;
	private Integer stat;
	private Date createTime;
	private Date checkinTime;
	private Date checkoutTime;
	private String remark;
	private String openPwd;
	private Date actualCheckinTime;
	private Date actualCheckoutTime;
	private String otaStayId;
	private Integer checkoutType;
	private String lodgerName;
	private String contactPhone;
	private Integer chkinPrice;
	private Integer parentCheckinId;
	private Integer childCheckinId;
	private Date lastModDtm;
	private Integer nightCount;
	private Integer chgRoomFlag;
	private Integer overstayFlag;
	private String batch;
	private Integer precheckoutFlag;

	public Integer getPrecheckoutFlag() {
		return precheckoutFlag;
	}

	public void setPrecheckoutFlag(Integer precheckoutFlag) {
		this.precheckoutFlag = precheckoutFlag;
	}

	public Integer getOverstayFlag() {
		return overstayFlag;
	}

	public void setOverstayFlag(Integer overstayFlag) {
		this.overstayFlag = overstayFlag;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Integer getChgRoomFlag() {
		return chgRoomFlag;
	}

	public void setChgRoomFlag(Integer chgRoomFlag) {
		this.chgRoomFlag = chgRoomFlag;
	}

	public Integer getNightCount() {
		return nightCount;
	}

	public void setNightCount(Integer nightCount) {
		this.nightCount = nightCount;
	}

	public Date getLastModDtm() {
		return lastModDtm;
	}

	public void setLastModDtm(Date lastModDtm) {
		this.lastModDtm = lastModDtm;
	}

	public Integer getParentCheckinId() {
		return parentCheckinId;
	}

	public void setParentCheckinId(Integer parentCheckinId) {
		this.parentCheckinId = parentCheckinId;
	}

	public Integer getChildCheckinId() {
		return childCheckinId;
	}

	public void setChildCheckinId(Integer childCheckinId) {
		this.childCheckinId = childCheckinId;
	}

	public Integer getChkinPrice() {
		return chkinPrice;
	}

	public void setChkinPrice(Integer chkinPrice) {
		this.chkinPrice = chkinPrice;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getLodgerName() {
		return lodgerName;
	}

	public void setLodgerName(String lodgerName) {
		this.lodgerName = lodgerName;
	}

	public Integer getCheckoutType() {
		return checkoutType;
	}

	public void setCheckoutType(Integer checkoutType) {
		this.checkoutType = checkoutType;
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

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getActualCheckinTime() {
		return actualCheckinTime;
	}

	public void setActualCheckinTime(Date actualCheckinTime) {
		this.actualCheckinTime = actualCheckinTime;
	}

	public Date getActualCheckoutTime() {
		return actualCheckoutTime;
	}

	public void setActualCheckoutTime(Date actualCheckoutTime) {
		this.actualCheckoutTime = actualCheckoutTime;
	}

	public Integer getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(Integer checkinId) {
		this.checkinId = checkinId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getLodgerId() {
		return lodgerId;
	}

	public void setLodgerId(Integer lodgerId) {
		this.lodgerId = lodgerId;
	}

	public Integer getStat() {
		return stat;
	}

	public void setStat(Integer stat) {
		this.stat = stat;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	@Override
	public String toString() {
		return "XbCheckin [checkinId=" + checkinId + ", orderId=" + orderId + ", lodgerId=" + lodgerId + ", roomId=" + roomId + ", stat=" + stat
				+ ", createTime=" + createTime + ", checkinTime=" + checkinTime + ", checkoutTime=" + checkoutTime + ", remark=" + remark
				+ ", openPwd=" + openPwd + ", actualCheckinTime=" + actualCheckinTime + ", actualCheckoutTime=" + actualCheckoutTime + ", otaStayId="
				+ otaStayId + ", checkoutType=" + checkoutType + ", lodgerName=" + lodgerName + ", contactPhone=" + contactPhone + ", chkinPrice="
				+ chkinPrice + ", parentCheckinId=" + parentCheckinId + ", childCheckinId=" + childCheckinId + ", lastModDtm=" + lastModDtm
				+ ", nightCount=" + nightCount + ", chgRoomFlag=" + chgRoomFlag + ", batch=" + batch + "]";
	}

}
