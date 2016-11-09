package cn.com.xbed.app.bean.vo;

public class CheckinerUnit {
	private int doesSendPin;// 1-发送 2-不发送
	private String idcardNo;
	private String mobile;
	private String name;

	public CheckinerUnit() {
		super();
	}

	public CheckinerUnit(int doesSendPin, String idcardNo, String mobile, String name) {
		super();
		this.doesSendPin = doesSendPin;
		this.idcardNo = idcardNo;
		this.mobile = mobile;
		this.name = name;
	}

	public int getDoesSendPin() {
		return doesSendPin;
	}

	public void setDoesSendPin(int doesSendPin) {
		this.doesSendPin = doesSendPin;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CheckinerUnit [doesSendPin=" + doesSendPin + ", idcardNo=" + idcardNo + ", mobile=" + mobile + ", name=" + name + "]";
	}

}
