package cn.com.xbed.app.bean;

import java.util.Date;

public class XbSmsTemplate implements java.io.Serializable {

	private Integer tplId;
	private String tplKey;
	private String smsContent;
	private Date createDtm;
	private String descri;
	private Integer maxTimes;

	public Integer getTplId() {
		return tplId;
	}

	public void setTplId(Integer tplId) {
		this.tplId = tplId;
	}

	public String getTplKey() {
		return tplKey;
	}

	public void setTplKey(String tplKey) {
		this.tplKey = tplKey;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getDescri() {
		return descri;
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

	public Integer getMaxTimes() {
		return maxTimes;
	}

	public void setMaxTimes(Integer maxTimes) {
		this.maxTimes = maxTimes;
	}

}
