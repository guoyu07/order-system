package cn.com.xbed.app.bean;

import java.util.Date;

public class LogOwnerCreate implements java.io.Serializable {

	private Integer logId;
	private Integer isNewCreate;
	private Integer lodgerId;
	private Date createDtm;
	private String params;
	private String result;
	private String remark;
	private String ownerName;
	private String ownerMobile;

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerMobile() {
		return ownerMobile;
	}

	public void setOwnerMobile(String ownerMobile) {
		this.ownerMobile = ownerMobile;
	}

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Integer getIsNewCreate() {
		return isNewCreate;
	}

	public void setIsNewCreate(Integer isNewCreate) {
		this.isNewCreate = isNewCreate;
	}

	public Integer getLodgerId() {
		return lodgerId;
	}

	public void setLodgerId(Integer lodgerId) {
		this.lodgerId = lodgerId;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
