package cn.com.xbed.app.bean;

import java.util.Date;

public class XbCustAdvice implements java.io.Serializable {

	private Integer custAdviceId;
	private String advContent;
	private String advSubject;
	private String contact;
	private Date createDtm;

	public Integer getCustAdviceId() {
		return custAdviceId;
	}

	public String getAdvSubject() {
		return advSubject;
	}

	public void setAdvSubject(String advSubject) {
		this.advSubject = advSubject;
	}

	public void setCustAdviceId(Integer custAdviceId) {
		this.custAdviceId = custAdviceId;
	}

	public String getAdvContent() {
		return advContent;
	}

	public void setAdvContent(String advContent) {
		this.advContent = advContent;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

}
