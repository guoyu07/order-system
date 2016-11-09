package cn.com.xbed.app.service.invoicemodule.vo;

public class Invoice {
	private String invoiceTitle;// 发票抬头,
	private String mailName;// 收件人
	private String contactNo;// 手机号
	private String mailAddr;// 详细地址
	private String postCode;// 邮编
	private int invoiceMoney;

	public Invoice(String invoiceTitle, String mailName, String contactNo, String mailAddr, String postCode, int invoiceMoney) {
		super();
		this.invoiceTitle = invoiceTitle;
		this.mailName = mailName;
		this.contactNo = contactNo;
		this.mailAddr = mailAddr;
		this.postCode = postCode;
		this.invoiceMoney = invoiceMoney;
	}

	public int getInvoiceMoney() {
		return invoiceMoney;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public String getMailName() {
		return mailName;
	}

	public String getContactNo() {
		return contactNo;
	}

	public String getMailAddr() {
		return mailAddr;
	}

	public String getPostCode() {
		return postCode;
	}

}
