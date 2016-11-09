package cn.com.xbed.app.bean;

import java.util.Date;

public class XbCalendarDaily implements java.io.Serializable {

	private Integer calId;
	private Integer roomId;
	private String curMonth;// 格式2015-10-27
	private String curDate;// 格式2015-10
	private Integer stat;
	private Integer price;
	private Date createDtm;
	private Date lastModDtm;
	private Integer checkinId;
	private Integer orderId;
	private String remark;

	public String getCurMonth() {
		return curMonth;
	}

	public void setCurMonth(String curMonth) {
		this.curMonth = curMonth;
	}

	public Integer getCalId() {
		return calId;
	}

	public void setCalId(Integer calId) {
		this.calId = calId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public String getCurDate() {
		return curDate;
	}

	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}

	public Integer getStat() {
		return stat;
	}

	public void setStat(Integer stat) {
		this.stat = stat;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public Date getLastModDtm() {
		return lastModDtm;
	}

	public void setLastModDtm(Date lastModDtm) {
		this.lastModDtm = lastModDtm;
	}

	public Integer getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(Integer checkinId) {
		this.checkinId = checkinId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "XbCalendarDaily [calId=" + calId + ", roomId=" + roomId + ", curDate=" + curDate + ", stat=" + stat + ", price=" + price
				+ ", createDtm=" + createDtm + ", lastModDtm=" + lastModDtm + ", checkinId=" + checkinId + ", orderId=" + orderId + ", remark="
				+ remark + "]";
	}

}
