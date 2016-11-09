package cn.com.xbed.app.bean;

import java.util.Date;

public class XbPromoConf implements java.io.Serializable {

	private Integer promoConfId;
	private String beginDate;
	private String endDate;
	private Integer promoPrice;
	private Integer roomGroupId;
	private Date createDtm;
	private String remark;

	public Integer getPromoConfId() {
		return promoConfId;
	}

	public void setPromoConfId(Integer promoConfId) {
		this.promoConfId = promoConfId;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getPromoPrice() {
		return promoPrice;
	}

	public void setPromoPrice(Integer promoPrice) {
		this.promoPrice = promoPrice;
	}

	public Integer getRoomGroupId() {
		return roomGroupId;
	}

	public void setRoomGroupId(Integer roomGroupId) {
		this.roomGroupId = roomGroupId;
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
		return "XbPromoConf [promoConfId=" + promoConfId + ", beginDate=" + beginDate + ", endDate=" + endDate
				+ ", promoPrice=" + promoPrice + ", roomGroupId=" + roomGroupId + ", createDtm=" + createDtm
				+ ", remark=" + remark + "]";
	}

}
