package cn.com.xbed.app.bean;

import java.util.Date;

public class XbCheckiner implements java.io.Serializable {

	private Integer checkinerId;
	private Integer checkinId;
	private Integer lodgerId;
	private Integer sendFlag;
	private String name;
	private String idcardNo;
	private String mobile;
	private Date createDtm;
	private String remark;
	private Integer isMain;//default 0  1代表主入住人　0代表普通入住人 -1 下订单人
	private Integer isDel;//default 0

	
	public Integer getIsMain() {
		return isMain;
	}

	public void setIsMain(Integer isMain) {
		this.isMain = isMain;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Integer getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(Integer sendFlag) {
		this.sendFlag = sendFlag;
	}

	public Integer getCheckinerId() {
		return checkinerId;
	}

	public void setCheckinerId(Integer checkinerId) {
		this.checkinerId = checkinerId;
	}

	public Integer getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(Integer checkinId) {
		this.checkinId = checkinId;
	}

	public Integer getLodgerId() {
		return lodgerId;
	}

	public void setLodgerId(Integer lodgerId) {
		this.lodgerId = lodgerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdcardNo() {
		return idcardNo;
	}

	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "XbCheckiner [checkinerId=" + checkinerId + ", checkinId=" + checkinId + ", lodgerId=" + lodgerId + ", sendFlag=" + sendFlag
				+ ", name=" + name + ", idcardNo=" + idcardNo + ", mobile=" + mobile + ", createDtm=" + createDtm + ", remark=" + remark + ", isMain="
				+ isMain + ", isDel=" + isDel + "]";
	}

}
