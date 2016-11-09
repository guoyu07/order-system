package cn.com.xbed.app.bean;

import java.util.Date;

public class XbCleanRec implements java.io.Serializable {

	private Integer applyId;
	private Integer handleSts;
	private Integer srcType;
	private Date createDtm;
	private Integer checkinId;
	private Integer bookOrderId;
	private Integer roomId;
	private String roomNo;
	private Integer chainId;
	private String chainName;
	private Integer lodgerId;
	private String lodgerName;
	private String mobile;
	private String remark;
	private Integer cleanOrderId;
	private String cleanerName;
	private String cleanerMobile;

	public Integer getApplyId() {
		return applyId;
	}

	public void setApplyId(Integer applyId) {
		this.applyId = applyId;
	}

	public Integer getHandleSts() {
		return handleSts;
	}

	public void setHandleSts(Integer handleSts) {
		this.handleSts = handleSts;
	}

	public Integer getSrcType() {
		return srcType;
	}

	public void setSrcType(Integer srcType) {
		this.srcType = srcType;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public Integer getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(Integer checkinId) {
		this.checkinId = checkinId;
	}

	public Integer getBookOrderId() {
		return bookOrderId;
	}

	public void setBookOrderId(Integer bookOrderId) {
		this.bookOrderId = bookOrderId;
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

	public Integer getChainId() {
		return chainId;
	}

	public void setChainId(Integer chainId) {
		this.chainId = chainId;
	}

	public String getChainName() {
		return chainName;
	}

	public void setChainName(String chainName) {
		this.chainName = chainName;
	}

	public Integer getLodgerId() {
		return lodgerId;
	}

	public void setLodgerId(Integer lodgerId) {
		this.lodgerId = lodgerId;
	}

	public String getLodgerName() {
		return lodgerName;
	}

	public void setLodgerName(String lodgerName) {
		this.lodgerName = lodgerName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getCleanOrderId() {
		return cleanOrderId;
	}

	public void setCleanOrderId(Integer cleanOrderId) {
		this.cleanOrderId = cleanOrderId;
	}

	public String getCleanerName() {
		return cleanerName;
	}

	public void setCleanerName(String cleanerName) {
		this.cleanerName = cleanerName;
	}

	public String getCleanerMobile() {
		return cleanerMobile;
	}

	public void setCleanerMobile(String cleanerMobile) {
		this.cleanerMobile = cleanerMobile;
	}

}
