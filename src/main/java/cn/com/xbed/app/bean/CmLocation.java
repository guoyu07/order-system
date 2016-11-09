package cn.com.xbed.app.bean;

import java.util.Date;

public class CmLocation implements java.io.Serializable {

	private Integer locId;
	private String locCode;
	private String locName;
	private String locLevel;
	private String parentCode;
	private Date createDtm;
	private String remark;

	@Override
	public String toString() {
		return "CmLocation [locId=" + locId + ", locCode=" + locCode + ", locName=" + locName + ", locLevel=" + locLevel + ", parentCode="
				+ parentCode + ", createDtm=" + createDtm + ", remark=" + remark + "]";
	}

	public Integer getLocId() {
		return locId;
	}

	public void setLocId(Integer locId) {
		this.locId = locId;
	}

	public String getLocCode() {
		return locCode;
	}

	public void setLocCode(String locCode) {
		this.locCode = locCode;
	}

	public String getLocName() {
		return locName;
	}

	public void setLocName(String locName) {
		this.locName = locName;
	}

	public String getLocLevel() {
		return locLevel;
	}

	public void setLocLevel(String locLevel) {
		this.locLevel = locLevel;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
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

}
