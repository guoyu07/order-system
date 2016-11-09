package cn.com.xbed.app.bean;

import java.util.Date;

public class XbInvoiceRec implements java.io.Serializable {
	;
	private Integer invoiceRecId;
	private Integer lodgerId;
	private String orderNo;
	private String invoiceTitle;
	private String mailName;
	private String contactNo;
	private String mailAddr;
	private String postCode;
	private String remark;
	private Date createDtm;
	private Integer invoiceMoney;

	public Integer getInvoiceMoney() {
		return invoiceMoney;
	}

	public void setInvoiceMoney(Integer invoiceMoney) {
		this.invoiceMoney = invoiceMoney;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getInvoiceRecId() {
		return invoiceRecId;
	}

	public void setInvoiceRecId(Integer invoiceRecId) {
		this.invoiceRecId = invoiceRecId;
	}

	public Integer getLodgerId() {
		return lodgerId;
	}

	public void setLodgerId(Integer lodgerId) {
		this.lodgerId = lodgerId;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public String getMailName() {
		return mailName;
	}

	public void setMailName(String mailName) {
		this.mailName = mailName;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getMailAddr() {
		return mailAddr;
	}

	public void setMailAddr(String mailAddr) {
		this.mailAddr = mailAddr;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "XbInvoiceRec [invoiceRecId=" + invoiceRecId + ", lodgerId=" + lodgerId + ", orderNo=" + orderNo + ", invoiceTitle=" + invoiceTitle
				+ ", mailName=" + mailName + ", contactNo=" + contactNo + ", mailAddr=" + mailAddr + ", postCode=" + postCode + ", remark=" + remark
				+ ", createDtm=" + createDtm + ", invoiceMoney=" + invoiceMoney + "]";
	}

};
