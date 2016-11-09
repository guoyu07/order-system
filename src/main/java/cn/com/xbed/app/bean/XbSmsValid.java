package cn.com.xbed.app.bean;

import java.util.Date;

public class XbSmsValid implements java.io.Serializable {

	private Integer validId;
	private String mobile;
	private String randomNo;
	private Date sendDtm;
	private Date expireDtm;

	public Integer getValidId() {
		return validId;
	}

	public void setValidId(Integer validId) {
		this.validId = validId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRandomNo() {
		return randomNo;
	}

	public void setRandomNo(String randomNo) {
		this.randomNo = randomNo;
	}

	public Date getSendDtm() {
		return sendDtm;
	}

	public void setSendDtm(Date sendDtm) {
		this.sendDtm = sendDtm;
	}

	public Date getExpireDtm() {
		return expireDtm;
	}

	public void setExpireDtm(Date expireDtm) {
		this.expireDtm = expireDtm;
	}

}
