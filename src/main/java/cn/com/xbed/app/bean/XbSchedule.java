package cn.com.xbed.app.bean;

import java.util.Date;

public class XbSchedule implements java.io.Serializable {

	private Integer scheduleId;
	private Integer roomId;
	private Date schDate;
	private Integer stat;
	private Integer price;
	private Integer mortgage;
	private String remark;

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
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

	public Integer getMortgage() {
		return mortgage;
	}

	public void setMortgage(Integer mortgage) {
		this.mortgage = mortgage;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
