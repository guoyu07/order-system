package cn.com.xbed.app.bean;

import java.util.Date;

public class LogOverstayAutoCheckin implements java.io.Serializable {

	private Integer logId;
	private Integer oriCheckinId;
	private Integer newCheckinId;
	private String succFlag;
	private String remark;
	private String errorMsg;
	private Date createDtm;

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Integer getOriCheckinId() {
		return oriCheckinId;
	}

	public void setOriCheckinId(Integer oriCheckinId) {
		this.oriCheckinId = oriCheckinId;
	}

	public Integer getNewCheckinId() {
		return newCheckinId;
	}

	public void setNewCheckinId(Integer newCheckinId) {
		this.newCheckinId = newCheckinId;
	}

	public String getSuccFlag() {
		return succFlag;
	}

	public void setSuccFlag(String succFlag) {
		this.succFlag = succFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

}
